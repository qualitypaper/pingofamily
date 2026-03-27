package com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary;

import com.qualitypaper.fluentfusion.controller.dto.request.RemoveWordPartRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.UpdateWordRequest;
import com.qualitypaper.fluentfusion.exception.VocabularyOwnerException;
import com.qualitypaper.fluentfusion.exception.notfound.NotFoundException;
import com.qualitypaper.fluentfusion.exception.notfound.UserVocabularyNotFoundException;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.DescTranslation;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordExample;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordExampleTranslation;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import com.qualitypaper.fluentfusion.repository.DescRepository;
import com.qualitypaper.fluentfusion.repository.DescTranslationRepository;
import com.qualitypaper.fluentfusion.repository.WordExampleTranslationRepository;
import com.qualitypaper.fluentfusion.repository.userVocabulary.UserVocabularyRepository;
import com.qualitypaper.fluentfusion.service.db.DbService;
import com.qualitypaper.fluentfusion.service.db.types.Compare;
import com.qualitypaper.fluentfusion.service.db.types.FilterSeparator;
import com.qualitypaper.fluentfusion.service.db.types.Join;
import com.qualitypaper.fluentfusion.service.email.FormResendService;
import com.qualitypaper.fluentfusion.service.pts.images.ImageSearchService;
import com.qualitypaper.fluentfusion.service.pts.s3.ObjectType;
import com.qualitypaper.fluentfusion.service.pts.s3.S3Service;
import com.qualitypaper.fluentfusion.service.user.UserDbService;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.WordDictionaryService;
import com.qualitypaper.fluentfusion.service.vocabulary.word.WordService;
import com.qualitypaper.fluentfusion.service.vocabulary.word.WordTranslationService;
import com.qualitypaper.fluentfusion.service.vocabulary.wordExample.WordExampleService;
import com.qualitypaper.fluentfusion.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserVocabularyAdminService {

    private final DbService dbService;
    private final UserVocabularyRepository userVocabularyRepository;
    private final WordService wordService;
    private final WordTranslationService wordTranslationService;
    private final WordDictionaryService wordDictionaryService;
    private final DescTranslationRepository descTranslationRepository;
    private final WordExampleService wordExampleService;
    private final DescRepository descRepository;
    private final S3Service s3Service;
    private final UserDbService userDbService;
    private final ImageSearchService imageSearchService;
    private final FormResendService formResendService;
    private final UserVocabularyService userVocabularyService;
    private final WordExampleTranslationRepository wordExampleTranslationRepository;

    @Value("${spring.web.resources.static-locations}")
    private String staticLocation;

    public void regenerateSound(Long userVocabularyId, boolean example) {

        dbService.getTransactionTemplate().executeWithoutResult(status -> {

            try {
                UserVocabulary userVocabulary = userVocabularyRepository.findById(userVocabularyId)
                        .orElseThrow(UserVocabularyNotFoundException::new);

                if (example) {
                    WordExampleTranslation wordExampleTranslation = userVocabulary.getWordExampleTranslation();
//                generateSound(wordExampleTranslation);

                    wordExampleTranslationRepository.save(wordExampleTranslation);
                } else {
                    WordTranslation wordTranslation = userVocabulary.getWordTranslation();
                    // TODO: refactor
//                    TextToSpeechService.SoundsResponse soundsResponse = textToSpeechService.generateSoundsOnWordTranslation(wordTranslation);
//                    wordTranslation.getWordFrom().setSoundUrl(soundsResponse.soundFrom());
//                    wordTranslation.getWordTo().setSoundUrl(soundsResponse.soundTo());
                    wordService.save(wordTranslation.getWordFrom());
                    wordService.save(wordTranslation.getWordTo());
                }
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }

    @Transactional
    public void reloadWordImage(long userVocabularyId) {
        UserVocabulary userVocabulary = userVocabularyRepository.findById(userVocabularyId)
                .orElseThrow(() -> new NotFoundException("User vocabulary wasn't found in method reloadWordImage()"));

        if (!isOwner(userVocabulary.getVocabulary(), SecurityContextHolder.getContext())) {
            throw new VocabularyOwnerException("Wrong vocabulary owner");
        }

        WordTranslation wordTranslation = userVocabulary.getWordTranslation();
        wordTranslation.getWordFrom().setImageUrl(null);
        String newImageUrl = imageSearchService.findImage(wordTranslation.getWordFrom().getWord(), userVocabulary.getVocabulary().getLearningLanguage(), true);
        wordTranslation.getWordFrom().setImageUrl(newImageUrl);
        wordTranslation.getWordTo().setImageUrl(newImageUrl);
        wordService.save(wordTranslation.getWordFrom());
        wordService.save(wordTranslation.getWordTo());
    }

    @Transactional
    public void regenerateImagesForAllWords(long vocabularyGroupId) {
        String userVocabularyTableName = DbService.getTableName(UserVocabulary.class);
        String wordTranslationTableName = DbService.getTableName(WordTranslation.class);
        String wordTableName = DbService.getTableName(Word.class);

        final List<Map<String, Object>> list = dbService.select(userVocabularyTableName,
                List.of(userVocabularyTableName + ".id", "wf.id as word_from_id", "wf.word as word_from",
                        "wf.language as learning_language", "wt.id as word_to_id"),
                List.of(Join.left(wordTranslationTableName,
                                userVocabularyTableName + ".word_translation_id", wordTranslationTableName + ".id"),
                        Join.left(wordTableName, "word_from_id", "wf.id", "wf"),
                        Join.left(wordTableName, "word_to_id", "wt.id", "wt")),

                List.of(Compare.eq(userVocabularyTableName + ".vocabulary_group_id", vocabularyGroupId)), "");

        for (Map<String, Object> e : list) {
            Long wordFromId = Long.valueOf(String.valueOf(e.get("word_from_id")));
            Long wordToId = Long.valueOf(String.valueOf(e.get("word_to_id")));
            String wordFrom = String.valueOf(e.get("word_from"));
            String learningLanguage = String.valueOf(e.get("learning_language"));

            String imageUrl = imageSearchService.findImage(wordFrom, Language.valueOf(learningLanguage), true);
            if (imageUrl != null) {
                try {
                    dbService.update(wordTableName, Map.of("image_url", imageUrl),
                            List.of(Compare.eq("id", wordFromId, FilterSeparator.OR),
                                    Compare.eq("id", wordToId)));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    @Transactional
    public void updateWords(List<UpdateWordRequest> updateWordRequests) {
        SecurityContext context = SecurityContextHolder.getContext();
        for (UpdateWordRequest updateWordRequest : updateWordRequests) {
            updateWord(updateWordRequest, context);
        }
    }

    /**
     * @param updateWordRequest
     * @param context
     * @apiNote all word updates update directly WORD_TRANSLATION, be careful
     */
    @Transactional
    public void updateWord(UpdateWordRequest updateWordRequest, SecurityContext context) {
        UserVocabulary userVocabulary = userVocabularyRepository.findById(updateWordRequest.userVocabularyId()).orElseThrow(() -> new NotFoundException("User vocabulary wasn't found in method updateWord()"));

        if (!isOwner(userVocabulary.getVocabulary(), context)) {
            throw new VocabularyOwnerException("Wrong vocabulary owner");
        }

        WordTranslation wordTranslation = userVocabulary.getWordTranslation();
        WordExampleTranslation wordExampleTranslation = userVocabulary.getWordExampleTranslation();

        if (updateWordRequest.partOfSpeech() != null && !updateWordRequest.partOfSpeech().equals(wordTranslation.getWordFrom().getPos())) {
            wordTranslation.getWordFrom().setPos(updateWordRequest.partOfSpeech());
            wordService.save(wordTranslation.getWordFrom());
        }

        if (updateWordRequest.word() != null && updateWordRequest.partOfSpeech() != null && !updateWordRequest.word().equals(wordTranslation.getWordFrom().getWord())) {

            CompletableFuture<Word> wordFrom = wordService.createAndSaveWord(updateWordRequest.word(), updateWordRequest.partOfSpeech(),
                    userVocabulary.getVocabulary().getLearningLanguage(),
                    wordService.determineWordType(updateWordRequest.word(), updateWordRequest.partOfSpeech(), userVocabulary.getVocabulary().getLearningLanguage()), false);

            wordTranslation.setWordFrom(wordFrom.join());
        }

        if (updateWordRequest.wordTranslation() != null && updateWordRequest.partOfSpeech() != null
                && !updateWordRequest.wordTranslation().equals(wordTranslation.getWordTo().getWord())) {

            CompletableFuture<Word> wordTo = wordService.createAndSaveWord(updateWordRequest.wordTranslation(),
                    updateWordRequest.partOfSpeech(), userVocabulary.getVocabulary().getNativeLanguage(),
                    wordService.determineWordType(updateWordRequest.wordTranslation(), updateWordRequest.partOfSpeech(), userVocabulary.getVocabulary().getNativeLanguage()),
                    false);

            wordTranslation.setWordTo(wordTo.join());
            wordTranslation.getWordFrom().setPos(updateWordRequest.partOfSpeech());
            wordService.save(wordTranslation.getWordFrom());
        }

        if (updateWordRequest.example() != null && !updateWordRequest.example().equals(wordExampleTranslation.getWordExampleFrom().getExample())) {
            WordExample wordExampleFrom = wordExampleService.createAndSaveWordExample(updateWordRequest.example()
                    , "", wordTranslation.getWordFrom());

            wordExampleTranslation.setWordExampleFrom(wordExampleFrom);
        }

        if (updateWordRequest.exampleTranslation() != null && !updateWordRequest.exampleTranslation().equals(wordExampleTranslation.getWordExampleTo().getExample())) {
            WordExample wordExampleTo = wordExampleService.createAndSaveWordExample(updateWordRequest.exampleTranslation()
                    , "", wordTranslation.getWordTo());

            wordExampleTranslation.setWordExampleTo(wordExampleTo);
        }

        if (wordTranslation.getWordFrom().getWordDictionary() != null && updateWordRequest.description() != null && !updateWordRequest.description().isEmpty()
                && !updateWordRequest.description().equals(wordTranslation.getWordFrom().getWordDictionary().getDesc().getDescription())) {
            wordTranslation.getWordFrom().getWordDictionary().getDesc().setDescription(updateWordRequest.description());

            descRepository.save(wordTranslation.getWordFrom().getWordDictionary().getDesc());
        }


        DescTranslation descTranslation;
        if (wordTranslation.getWordTo().getWordDictionary() == null) {
            descTranslation = null;
        } else {
            descTranslation = wordTranslation.getWordTo().getWordDictionary().getDesc().getDescriptionTranslations()
                    .stream().filter(e -> e.getLanguage()
                            .equals(userVocabulary.getVocabulary().getNativeLanguage())).findFirst().orElse(null);
        }

        if (wordTranslation.getWordTo().getWordDictionary() != null && descTranslation != null && updateWordRequest.descriptionTranslation() != null
                && !updateWordRequest.descriptionTranslation().equals(descTranslation.getDescriptionTranslation())) {

            wordTranslation.getWordTo().getWordDictionary().getDesc().getDescriptionTranslations().stream().filter(e -> e.getLanguage()
                            .equals(userVocabulary.getVocabulary().getNativeLanguage())).findFirst().get()
                    .setDescriptionTranslation(updateWordRequest.descriptionTranslation());

            descTranslationRepository.save(wordTranslation.getWordTo().getWordDictionary().getDesc().getDescriptionTranslations().stream().filter(e -> e.getLanguage()
                    .equals(userVocabulary.getVocabulary().getNativeLanguage())).findFirst().get());
        }

        if (wordTranslation.getWordFrom().getWordDictionary() != null && updateWordRequest.synonyms() != null
                && !updateWordRequest.synonyms().isEmpty()
                && !updateWordRequest.synonyms().equals(wordTranslation.getWordFrom().getWordDictionary().getSynonyms())) {
            wordTranslation.getWordFrom().getWordDictionary().setSynonyms(updateWordRequest.synonyms());
            wordDictionaryService.save(wordTranslation.getWordFrom().getWordDictionary());
        }

        wordTranslationService.save(wordTranslation);
        wordExampleTranslationRepository.save(wordExampleTranslation);
    }

    @Transactional
    public void changeWordImage(Long userVocabularyId, MultipartFile file) {
        UserVocabulary userVocabulary = userVocabularyRepository.findById(userVocabularyId)
                .orElseThrow(UserVocabularyNotFoundException::new);

        Path to = Path.of(staticLocation);
        String filename = FileUtils.saveFromMultiPartFile(file, userVocabulary.getVocabulary().getCreatedBy(), to);
        String url = s3Service.uploadObject(filename, ObjectType.IMAGE);

        userVocabulary.getWordTranslation().getWordFrom().setImageUrl(url);
        wordService.save(userVocabulary.getWordTranslation().getWordFrom());
    }

    private boolean isOwner(Vocabulary vocabulary, SecurityContext context) throws ClassCastException {
        User user = userDbService.getUser(context);

        return vocabulary.getCreatedBy().getId().equals(user.getId());
    }

    public void regenerateConjugations(long userVocabularyId) {
        UserVocabulary userVocabulary = userVocabularyRepository.findById(userVocabularyId)
                .orElseThrow(UserVocabularyNotFoundException::new);

        if (!isOwner(userVocabulary.getVocabulary(), SecurityContextHolder.getContext())) {
            throw new VocabularyOwnerException("Wrong vocabulary owner");
        }

        wordService.regenerateConjugations(userVocabulary.getWordTranslation().getWordFrom());
    }

    @Async
    public void regenerateConjugations(List<Long> userVocabularyIds) {
        for (Long id : userVocabularyIds) {
            if (id == null) continue;

            regenerateConjugations(id);
        }
    }

    @Async
    public void removeWordPartFromWords(RemoveWordPartRequest removeWordPartRequest, SecurityContext securityContext) {
        formResendService.sendInfoMessage("Started remove word part from words: " + removeWordPartRequest.needle()
                + " " + removeWordPartRequest.userVocabularyIds().toString());

        for (Long id : removeWordPartRequest.userVocabularyIds()) {
            removeWordPart(removeWordPartRequest.needle(), id, securityContext);
        }
    }

    public void removeWordPart(String needle, Long userVocabularyId, SecurityContext context) {
        UserVocabulary userVocabulary = userVocabularyRepository.findById(userVocabularyId)
                .orElseThrow(() -> new NotFoundException("User vocabulary wasn't found in method updateWord()"));

        if (!isOwner(userVocabulary.getVocabulary(), context)) {
            throw new VocabularyOwnerException("Wrong vocabulary owner");
        }

        Word newWord = wordService.removeWordPart(userVocabulary.getWordTranslation().getWordFrom(), needle);
        if (newWord == null) return;

        userVocabularyService.deleteWord(userVocabularyId, userVocabulary.getVocabulary().getCreatedBy());

        WordTranslation newWordTranslation = wordTranslationService.createAndSave(newWord, userVocabulary.getWordTranslation().getWordTo());
        userVocabularyService.addAlreadyCreatedWord(newWordTranslation,
                userVocabulary.getVocabularyGroup(),
                null,
                null
        );
    }
}
