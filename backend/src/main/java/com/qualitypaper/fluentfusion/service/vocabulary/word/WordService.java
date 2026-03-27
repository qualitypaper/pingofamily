package com.qualitypaper.fluentfusion.service.vocabulary.word;

import com.qualitypaper.fluentfusion.exception.notfound.NotFoundException;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.WordDictionary;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordType;
import com.qualitypaper.fluentfusion.repository.PossibleTranslationByLanguageRepository;
import com.qualitypaper.fluentfusion.repository.PossibleTranslationRepository;
import com.qualitypaper.fluentfusion.repository.WordRepository;
import com.qualitypaper.fluentfusion.service.email.FormResendService;
import com.qualitypaper.fluentfusion.service.pts.dictionary.LookUpService;
import com.qualitypaper.fluentfusion.service.pts.images.ImageSearchService;
import com.qualitypaper.fluentfusion.service.pts.tts.TextToSpeechService;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.WordDictionaryService;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.AddWord;
import com.qualitypaper.fluentfusion.util.HttpUtils;
import io.micrometer.common.lang.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class WordService {

    private final WordRepository wordRepository;
    private final LookUpService lookUpService;
    private final HttpUtils httpService;
    private final WordDictionaryService wordDictionaryService;
    private final UnneededWordsService unneededWordsService;
    private final PossibleTranslationRepository possibleTranslationRepository;
    private final PossibleTranslationByLanguageRepository possibleTranslationByLanguageRepository;
    private final TextToSpeechService textToSpeechService;
    private final ImageSearchService imageSearchService;
    private final FormResendService formResendService;

    @Value("${url.static.audio}")
    private String staticAudioUrl;


    public static boolean checkValidity(Word word) {

        return word != null
                && word.getWord() != null
                && word.getWordDictionary() != null
                && WordDictionaryService.checkValidity(word.getWordDictionary());
    }

    public static String mapPos(String pos) {
        return pos != null ? switch (pos.toLowerCase()) {
            case "noun", "propn" -> "NOUN";
            case "verb" -> "VERB";
            case "adj" -> "ADJECTIVE";
            case "adv" -> "ADVERB";
            default -> "OTHER";
        } : "OTHER";
    }

    @Transactional(readOnly = true)
    public Optional<Word> findById(Long id) {
        return wordRepository.findById(id);
    }

    @Async
    @Transactional
    public CompletableFuture<Word> createAndSaveWord(String word, PartOfSpeech partOfSpeech,
                                                     Language language, WordType wordType, boolean alreadyCreatedCheck) {
        if (alreadyCreatedCheck) {
            Optional<Word> alreadyCreated = getWord(word, language, partOfSpeech.name());
            if (alreadyCreated.isPresent()) {
                log.info("Word {} already exists", word);
                return CompletableFuture.completedFuture(alreadyCreated.get());
            }
        }

        Word wordToSave;
        AddWord addWord = new AddWord(word, wordType);
        wordToSave = createWord(addWord, language, partOfSpeech);

        if (unneededWordsService.isNeeded(word)) {
            wordToSave.setWordDictionary(wordDictionaryService.addConjugationToWord(wordToSave));
        } else wordToSave.setWordDictionary(null);

        String soundKey = textToSpeechService.generateSoundFile(wordToSave.getWord(), wordToSave.getLanguage());
        wordToSave.setSoundUrl(soundKey);
        wordRepository.saveAndFlush(wordToSave);

        return CompletableFuture.completedFuture(wordToSave);
    }

    @Async
    @Transactional
    public CompletableFuture<Word> createAndSaveWord(AddWord addWord, Language language) {
        PartOfSpeech partOfSpeech = WordDictionaryService.mapPos(addWord.getTranslation().getPos());
        WordType wordType = addWord.getWordType();

        Optional<Word> alreadyCreated = getWord(addWord.getWord(), language, partOfSpeech.name());
        if (alreadyCreated.isPresent()) return CompletableFuture.completedFuture(alreadyCreated.get());

        Word wordToSave = createWord(addWord, language, partOfSpeech);

        if (wordType.equals(WordType.WORD) || wordType.equals(WordType.PHRASAL_VERB)
                || wordType.equals(WordType.VERB_WITH_PREPOSITION)
                || wordType.equals(WordType.GERMAN_REFLEXIVE_VERB)
                || wordType.equals(WordType.SPANISH_REFLEXIVE_VERB)) {

            wordToSave.setWordDictionary(wordDictionaryService.addConjugationToWord(wordToSave));
        } else {
            wordToSave.setWordDictionary(wordDictionaryService.createAndSaveEmptyEntity(wordToSave));
        }

        return CompletableFuture.completedFuture(wordRepository.saveAndFlush(wordToSave));
    }

    public WordType determineWordType(String word, @Nullable PartOfSpeech pos, Language language) {
        if (language.equals(Language.SPANISH) && word.endsWith("se")) return WordType.SPANISH_REFLEXIVE_VERB;

        int wordCount = unneededWordsService.removeUnneededWords(word, pos, language);

        return switch (wordCount) {
            case 1 -> WordType.WORD;
            case 2, 3 -> {
                if (pos != null && pos.equals(PartOfSpeech.VERB)) {
                    if (unneededWordsService.isPhrasalVerb(word)) yield WordType.PHRASAL_VERB;
                    else if (unneededWordsService.isGermanSpreadVerb(word)) yield WordType.GERMAN_REFLEXIVE_VERB;
                }


                yield WordType.PHRASE;
            }
            case 0 -> WordType.NO_NEED_TO_CHECK;
            default -> WordType.SENTENCE;
        };
    }

    protected Word createWord(AddWord addWord, Language language, PartOfSpeech partOfSpeech) {
        String imageUrl = imageSearchService.findImage(addWord.getWord(), language, true);
        String soundUrl = textToSpeechService.generateSoundFile(addWord.getWord(), language);

        Word wordToSave = Word.builder()
                .word(addWord.getWord())
                .pos(partOfSpeech)
                .language(language)
                .wordType(addWord.getWordType())
                .imageUrl(imageUrl)
                .soundUrl(soundUrl)
                .createdAt(LocalDateTime.now())
                .build();

        return wordRepository.save(wordToSave);
    }

    public Optional<Word> getWord(String word, Language language, String pos) {
        if (pos == null) return wordRepository.findByWordAndLanguage(word, language);
        PartOfSpeech partOfSpeech = WordDictionaryService.mapPos(pos);

        return wordRepository.findTopByWordAndPosAndLanguage(word, partOfSpeech, language);
    }

    public List<Word> getWord(String word, Language language) {
        return wordRepository.findAllByWordAndLanguage(word, language);
    }

    public Word save(Word word) {
        return wordRepository.save(word);
    }

    public void delete(Word word) {
        wordRepository.delete(word);
    }

    public void regenerateSound(long id) {
        Word word = wordRepository.findById(id).orElse(null);
        if (word == null) return;
        else if (word.getWord() == null || word.getWord().isEmpty()) {
            wordRepository.delete(word);
            return;
        }

        String soundUrl = textToSpeechService.generateSoundFile(word.getWord(), word.getLanguage()) + ".mp3";
        word.setSoundUrl(soundUrl);
        wordRepository.save(word);
    }

    @Async
    @Transactional
    public void regenerateConjugations(long id) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Word with id: " + id + " wasn't found"));

        WordDictionary wordDictionary = wordDictionaryService.addConjugationToWord(word);
        word.setWordDictionary(wordDictionary);
        wordRepository.save(word);
    }

    @Async
    @Transactional
    public void regenerateConjugations(Word word) {
        WordDictionary wordDictionary = wordDictionaryService.addConjugationToWord(word);
        word.setWordDictionary(wordDictionary);
        wordRepository.save(word);
    }

    public Word removeWordPart(Word wordFrom, String needle) {
        String word = wordFrom.getWord();

        if (!word.contains(needle)) {
            log.info("Word {} doesn't contain needle {}", word, needle);
            return null;
        }

        String newWord = word.replace(needle, "").trim();
        PartOfSpeech pos = wordFrom.getPos();
        Language language = wordFrom.getLanguage();

        CompletableFuture<Word> newWordFuture =
                createAndSaveWord(newWord, pos, language,
                        determineWordType(newWord, pos, language), true);

        try {
            return newWordFuture.join();
        } catch (Exception e) {
            formResendService.sendErrorMessage(e);
            throw new RuntimeException(e);
        }
    }
}
