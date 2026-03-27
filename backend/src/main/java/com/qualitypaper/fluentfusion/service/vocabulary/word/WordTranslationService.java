package com.qualitypaper.fluentfusion.service.vocabulary.word;

import com.qualitypaper.fluentfusion.controller.dto.request.AddWordRequest;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.TempWordStorage;
import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslationType;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordType;
import com.qualitypaper.fluentfusion.repository.WordTranslationRepository;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.AddWord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class WordTranslationService {

    private final WordTranslationRepository wordTranslationRepository;
    private final WordService wordService;

    private final Random random = new Random();


    public static boolean checkValidity(WordTranslation wordTranslation) {

        return wordTranslation != null
                && wordTranslation.getWordFrom() != null
                && wordTranslation.getWordTo() != null
                && WordService.checkValidity(wordTranslation.getWordFrom())
                && WordService.checkValidity(wordTranslation.getWordTo());
    }

    public WordTranslation createAndSave(Word wordFrom, Word wordTo, WordTranslationType wordTranslationType) {
        var wordTranslation = create(wordFrom, wordTo, wordTranslationType);
        wordTranslationRepository.save(wordTranslation);
        return wordTranslation;
    }

    public WordTranslation createAndSave(Word wordFrom, Word wordTo) {
        return createAndSave(wordFrom, wordTo, WordTranslationType.SYSTEM);
    }


    @Transactional
    public WordTranslation createAndSave(AddWord addWord) {
        Vocabulary vocabulary = addWord.getVocabulary();
        PartOfSpeech wordToPos;

        try {
            wordToPos = PartOfSpeech.valueOf(Objects.requireNonNull(addWord.getTranslation().getPos()).toUpperCase());
        } catch (IllegalArgumentException e) {
            wordToPos = PartOfSpeech.OTHER;
        }

        WordType wordToWordType = wordService.determineWordType(addWord.getTranslation().getTranslation(), wordToPos, vocabulary.getNativeLanguage());
        log.info("Determined word type for WORD_FROM -> {}", addWord.getWordType());
        log.info("Determined word type for WORD_TO ->  {}", wordToWordType);

        CompletableFuture<Word> wordFrom = wordService.createAndSaveWord(addWord, vocabulary.getLearningLanguage());

        AddWord addWordToCopy = AddWord.copyWithNewWordAndWordType(addWord, addWord.getTranslation().getTranslation(), wordToWordType);
        CompletableFuture<Word> wordTo = wordService.createAndSaveWord(
                addWordToCopy, vocabulary.getNativeLanguage()
        );

        return createAndSave(wordFrom.join(), wordTo.join());
    }

    private WordTranslation create(Word word1, Word word2, WordTranslationType wordTranslationType) {
        return WordTranslation.builder()
                .wordFrom(word1)
                .wordTo(word2)
                .wordTranslationType(wordTranslationType)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public Optional<WordTranslation> getWordTranslation(Word wordFrom) {
        return wordTranslationRepository.findTopByWordFromAndWordTranslationType(wordFrom, WordTranslationType.SYSTEM);
    }

    public List<WordTranslation> getAllByWord(Word word) {
        return wordTranslationRepository.findAllByWord(word);
    }

    public Optional<WordTranslation> getWordTranslation(Word wordFrom, Word wordTo) {
        return wordTranslationRepository.findTopByWordFromAndWordToAndWordTranslationType(wordFrom, wordTo,
                WordTranslationType.SYSTEM);
    }

    public long getWordTranslationIdByWords(String wordFrom, String wordTo) {
        List<Long> wordTranslationIds = wordTranslationRepository.findByWordFromAndWordTo(wordFrom, wordTo);
        if (wordTranslationIds.isEmpty()) return -1;

        int index = random.nextInt(wordTranslationIds.size());
        return wordTranslationIds.get(index);
    }

    public WordTranslation save(WordTranslation wordTranslation) {
        return wordTranslationRepository.save(wordTranslation);
    }

    public void delete(WordTranslation alreadyCreated) {
        alreadyCreated.setWordTo(null);
        alreadyCreated.setWordFrom(null);
        wordTranslationRepository.delete(alreadyCreated);
    }

    @Transactional
    public Optional<WordTranslation> isAlreadyCreated(AddWordRequest addWordRequest, Language learningLanguage, Language nativeLanguage) {
        Optional<Word> wordFrom = wordService.getWord(addWordRequest.getWord(), learningLanguage, addWordRequest.getTranslationJson().getPos());
        Optional<Word> wordTo = wordService.getWord(addWordRequest.getTranslationJson().getTranslation(), nativeLanguage, addWordRequest.getTranslationJson().getPos());

        if (wordFrom.isPresent() && wordTo.isPresent())
            return getWordTranslation(wordFrom.get(), wordTo.get());

        return Optional.empty();
    }

    @Transactional
    public WordTranslation createAndSave(TempWordStorage tempWordStorage) {

        PartOfSpeech partOfSpeech = tempWordStorage.getPartOfSpeech();
        Optional<Word> optionalWordFrom = wordService.getWord(tempWordStorage.getWord(), tempWordStorage.getLearningLanguage(), partOfSpeech.name());

        CompletableFuture<Word> wordFrom = optionalWordFrom.map(CompletableFuture::completedFuture).orElseGet(() -> wordService.createAndSaveWord(tempWordStorage.getWord(), partOfSpeech,
                tempWordStorage.getLearningLanguage(),
                wordService.determineWordType(tempWordStorage.getWord(), tempWordStorage.getPartOfSpeech(),
                        tempWordStorage.getLearningLanguage()), true));

        Optional<Word> optionalWordTo = wordService.getWord(tempWordStorage.getTranslation(), tempWordStorage.getNativeLanguage(), partOfSpeech.name());

        CompletableFuture<Word> wordTo = optionalWordTo.map(CompletableFuture::completedFuture).orElseGet(() -> wordService.createAndSaveWord(tempWordStorage.getTranslation(), partOfSpeech,
                tempWordStorage.getNativeLanguage(),
                wordService.determineWordType(tempWordStorage.getTranslation(), tempWordStorage.
                        getPartOfSpeech(), tempWordStorage.getNativeLanguage()), true));

        return createAndSave(wordFrom.join(), wordTo.join());
    }

}
