package com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary;

import com.qualitypaper.fluentfusion.controller.dto.request.MoveWordsRequest;
import com.qualitypaper.fluentfusion.exception.VocabularyOwnerException;
import com.qualitypaper.fluentfusion.exception.notfound.UserVocabularyNotFoundException;
import com.qualitypaper.fluentfusion.model.user.Role;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.*;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.Training;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TrainingType;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExample;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleData;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import com.qualitypaper.fluentfusion.repository.TrainingExampleDataRepository;
import com.qualitypaper.fluentfusion.repository.TrainingExampleRepository;
import com.qualitypaper.fluentfusion.repository.TrainingRepository;
import com.qualitypaper.fluentfusion.repository.WordTranslationRepository;
import com.qualitypaper.fluentfusion.repository.userVocabulary.UserVocabularyRepository;
import com.qualitypaper.fluentfusion.service.db.DbService;
import com.qualitypaper.fluentfusion.service.db.SqlQuery;
import com.qualitypaper.fluentfusion.service.db.queries.GetWordsByVocabularyAndVocabularyGroupId;
import com.qualitypaper.fluentfusion.service.db.queries.resultTypes.GetWords;
import com.qualitypaper.fluentfusion.service.db.types.Compare;
import com.qualitypaper.fluentfusion.service.db.types.Join;
import com.qualitypaper.fluentfusion.service.db.types.SqlValue;
import com.qualitypaper.fluentfusion.service.pts.translation.TranslationService;
import com.qualitypaper.fluentfusion.service.pts.tts.TextToSpeechService;
import com.qualitypaper.fluentfusion.service.user.UserDbService;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.TrainingExampleDataService;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.TranslationDirection;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabulary.VocabularyService;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabularyGroup.VocabularyGroupService;
import com.qualitypaper.fluentfusion.service.vocabulary.word.TranslationJson;
import com.qualitypaper.fluentfusion.service.vocabulary.word.WordTranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserVocabularyScriptService {

  private final TrainingExampleRepository trainingExampleRepository;
  private final TrainingExampleDataRepository trainingExampleDataRepository;
  private final VocabularyGroupService vocabularyGroupService;
  private final UserVocabularyService userVocabularyService;
  private final DbService dbService;
  private final TranslationService translationService;
  private final WordTranslationService wordTranslationService;
  private final UserVocabularyRepository userVocabularyRepository;
  private final TrainingExampleDataService trainingExampleDataService;
  private final TextToSpeechService textToSpeechService;
  private final TrainingRepository trainingRepository;
  private final WordTranslationRepository wordTranslationRepository;
  private final UserDbService userDbService;

  private static int getTrainingExampleCount(List<TrainingExample> trainingExamples, TrainingType duplicateType) {
    int count = 0;
    for (TrainingExample trainingExample : trainingExamples) {
      if (trainingExample.getTrainingExampleData() == null ||
              trainingExample.getTrainingExampleData().getTrainingType() == null) {
        continue;
      }

      if (trainingExample.getTrainingExampleData().getTrainingType().equals(duplicateType)) {
        count++;
      }
    }
    return count;
  }

  @Async
  public void fixTraining(TrainingType duplicateType, TrainingType correctType) {
    int pageSize = 10;

    for (int i = 0; i < 1000; i++) {
      List<Training> trainingList = userVocabularyRepository.findByTrainingType(duplicateType,
              i + pageSize, pageSize * i);
      if (trainingList.isEmpty()) break;

      for (Training training : trainingList) {
        List<TrainingExample> trainingExamples = training.getTrainingExamples();
        int count = getTrainingExampleCount(trainingExamples, duplicateType);
        if (count <= 1) continue;

        Optional<TrainingExample> first = trainingExamples
                .stream()
                .filter(e -> e.getTrainingExampleData()
                        .getTrainingType().equals(duplicateType))
                .findFirst();
        if (first.isEmpty()) continue;

        TrainingExampleData trainingExampleData = first.get().getTrainingExampleData();
        TrainingExampleData trainingExampleData1 = trainingExampleDataService.createAndSave(
                new TrainingExampleDataService.TrainingExampleParams(
                        trainingExampleData.getWordTranslation(),
                        training,
                        trainingExampleData.getSentence(),
                        trainingExampleData.getSentenceTranslation(),
                        trainingExampleData.getSoundUrl(),
                        trainingExampleData.getTranslationDirection(),
                        correctType,
                        new String[]{trainingExampleData.getFormattedString(), trainingExampleData.getIdentifiedWord()}
                ));
        first.get().setTrainingExampleData(trainingExampleData1);
        trainingExampleRepository.save(first.get());
        log.info("Update Training Example with id: {}", first.get().getId());
      }
    }
  }

  @Async
  @Transactional
  public void removeNullReferencedTrainingExamples() {
    List<Map<String, Object>> select = dbService.select(Training.class, List.of("training.id"),
            List.of(Join.left("user_vocabulary", "user_vocabulary.next_training_id", "training.id")),
            List.of(Compare.is("user_vocabulary.id", SqlValue.NULL)), "");

    int pageSize = 10;
    for (int i = 0; i < select.size(); i += pageSize) {

      trainingRepository.deleteAllById(select
              .stream()
              .skip(i)
              .map(e -> (Long) e.get("id"))
              .limit(pageSize)
              .toList()
      );
      log.info("Removed {} training examples", pageSize);
    }
  }

  public void moveWords(MoveWordsRequest request) {
    User user = userDbService.getUser(SecurityContextHolder.getContext());

    VocabularyGroup vocabularyGroupFrom = vocabularyGroupService.findById(request.vocabularyGroupIdFrom());
    VocabularyGroup vocabularyGroupTo = vocabularyGroupService.findById(request.vocabularyGroupIdTo());

    if ((vocabularyGroupFrom.getType().equals(VocabularyGroupType.PREDEFINED)
            || vocabularyGroupTo.getType().equals(VocabularyGroupType.PREDEFINED))
            && !user.getRole().equals(Role.ROLE_ADMIN)) {

      throw new IllegalStateException("Permission denied");
    } else if (!vocabularyGroupFrom.getVocabulary().getId().equals(vocabularyGroupTo.getVocabulary().getId())) {
      throw new IllegalStateException("Vocabulary groups must belong to the same vocabulary");
    }

    Vocabulary vocabulary = vocabularyGroupFrom.getVocabulary();
    if (vocabulary.getCreatedBy().getId().equals(user.getId())) {
      moveWords(request.userVocabularyIds(), vocabularyGroupFrom, vocabularyGroupTo, user);
    } else {
      throw new VocabularyOwnerException("Permission denied");
    }
  }

  private void moveWords(List<Long> longs, VocabularyGroup vocabularyGroupFrom, VocabularyGroup vocabularyGroupTo, User user) {
    if (longs == null || longs.isEmpty() || vocabularyGroupFrom == null || vocabularyGroupTo == null) return;

    CompletableFuture.runAsync(() -> {
      for (Long id : longs) {
        moveWord(id, vocabularyGroupFrom, vocabularyGroupTo, user);
      }
    });
  }

  private void moveWord(Long id, VocabularyGroup vocabularyGroupFrom, VocabularyGroup vocabularyGroupTo, User user) {
    SqlQuery getWords = new GetWordsByVocabularyAndVocabularyGroupId(vocabularyGroupFrom.getVocabulary().getId(), vocabularyGroupTo.getId());
    List<GetWords> words = dbService.execute(getWords);

    UserVocabulary userVocabulary = userVocabularyRepository.findById(id)
            .orElseThrow(UserVocabularyNotFoundException::new);
    TranslationJson translation = new TranslationJson(userVocabulary.getWordTranslation().getWordTo().getWord(),
            userVocabulary.getWordTranslation().getWordTo().getPos().name(), null);

    if (!userVocabularyService.checkUniqueWord(words,
            userVocabulary.getWordTranslation().getWordFrom().getWord(),
            translation,
            vocabularyGroupTo.getId())
    ) {
      log.error("Word: {} already exists in the target vocabulary group: {}",
              userVocabulary.getWordTranslation().getWordFrom().getWord(),
              vocabularyGroupTo.getId());
      userVocabularyService.deleteWord(userVocabulary.getId(), user);
      return;
    }

    userVocabularyService.deleteWord(userVocabulary.getId(), user);
  }

  public TempWordStorage getTempStorage(String word, String translation, VocabularyGroup vocabularyGroup) {
    PartOfSpeech pos;

    try {
      pos = translationService.getDetailedPos(
              translation,
              word,
              vocabularyGroup.getVocabulary().getNativeLanguage(),
              vocabularyGroup.getVocabulary().getLearningLanguage()
      );
    } catch (IllegalStateException _) {
      pos = PartOfSpeech.valueOf(translationService.getPos(translation, vocabularyGroup.getVocabulary().getNativeLanguage()).toUpperCase());
    }


    return TempWordStorage.builder()
            .word(word)
            .translation(translation)
            .partOfSpeech(pos)
            .vocabularyGroup(vocabularyGroup)
            .nativeLanguage(vocabularyGroup.getVocabulary().getNativeLanguage())
            .learningLanguage(vocabularyGroup.getVocabulary().getLearningLanguage())
            .build();
  }

  public void fixSounds() {
    List<Object[]> tedList = trainingExampleDataRepository.findAllBySoundUrl("");
    List<Object[]> tedList1 = trainingExampleDataRepository.findAllBySoundUrl(null);
    tedList.addAll(tedList1);

    for (Object[] ted : tedList) {
      Long id = (Long) ted[0];
      TranslationDirection translationDirection = (TranslationDirection) ted[2];
      WordTranslation wordTranslation = (WordTranslation) ted[3];
      String sentence = (String) ted[1];
      String key = textToSpeechService.generateSoundFile(sentence,
              translationDirection.equals(TranslationDirection.INITIAL)
                      ? wordTranslation.getWordFrom().getLanguage()
                      : wordTranslation.getWordTo().getLanguage()
      );

      dbService.update(TrainingExampleData.class, Map.of("sound_url", key),
              List.of(Compare.eq("id", id)));
    }
  }


  @Async
  @Transactional
  public void addWordScript(final TempWordStorage tempWordStorage) {
    if (tempWordStorage == null || tempWordStorage.getVocabularyGroup() == null) return;

    long wordTranslationIdByWords = wordTranslationService.getWordTranslationIdByWords(tempWordStorage.getWord(), tempWordStorage.getTranslation());
    Optional<WordTranslation> wordTranslation = getWordTranslation(tempWordStorage, wordTranslationIdByWords);

    if (wordTranslation.isPresent() && !tempWordStorage.getVocabularyGroup().getName().equals(VocabularyService.DEFAULT)) {
      userVocabularyService.addAlreadyCreatedWord(
              wordTranslation.get(), tempWordStorage.getVocabularyGroup(), null, null
      );
    }
  }

  private Optional<WordTranslation> getWordTranslation(TempWordStorage tempWordStorage, long wordTranslationIdByWords) {
    Optional<WordTranslation> wordTranslation;
    if (wordTranslationIdByWords == -1) {
      wordTranslation = Optional.of(wordTranslationService.createAndSave(tempWordStorage));
    } else {
      wordTranslation = wordTranslationRepository.findById(wordTranslationIdByWords);
    }

    return wordTranslation;
  }

}
