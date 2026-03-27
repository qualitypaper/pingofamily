package com.qualitypaper.fluentfusion.service.vocabulary.learning;

import com.qualitypaper.fluentfusion.controller.dto.request.*;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.TrainingExampleResponse;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.TrainingGenerationResponse;
import com.qualitypaper.fluentfusion.exception.VocabularyOwnerException;
import com.qualitypaper.fluentfusion.exception.notfound.NotFoundException;
import com.qualitypaper.fluentfusion.mappers.wordTranslation.WordTranslationMapper;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroup;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroupType;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.LearningSession;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TrainingType;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExample;
import com.qualitypaper.fluentfusion.repository.LearningSessionRepository;
import com.qualitypaper.fluentfusion.service.user.UserDbService;
import com.qualitypaper.fluentfusion.service.user.UserStreakService;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample.TrainingExampleMistakeData;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample.TrainingExampleResultData;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample.TrainingExampleService;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.TrainingMistakeWithScore;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.UserVocabularyDbService;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabularyGroup.VocabularyGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class LearningService {
  public static final List<TrainingType> needWordsTranslation = List.of(
      TrainingType.COMPLETE_EMPTY_SPACES,
      TrainingType.PHRASE_CONSTRUCTION,
      TrainingType.PHRASE_CONSTRUCTION_REVERSED);
  protected static final int TRAINING_EXAMPLES_COUNT = 3;

  private final LearningSessionRepository learningSessionRepository;
  private final TrainingExampleService trainingExampleService;
  private final UserVocabularyDbService userVocabularyDbService;
  private final UserStreakService userStreakService;
  private final UserDbService userDbService;
  private final TrainingService trainingService;
  private final TrainingCompleteService trainingCompleteService;
  private final VocabularyGroupService vocabularyGroupService;
  private final WordTranslationMapper wordTranslationMapper;

  @Value("${learning.number-of-words-per-training}")
  private int numberOfWordsPerTraining;
  @Value("${learning.new-words-portion:0.6}")
  private float newWordsPortion;

  @Transactional
  public TrainingGenerationResponse generate(TrainingGenerationRequest request) {
    User user = userDbService.getUser(SecurityContextHolder.getContext());
    VocabularyGroup vocabularyGroup = vocabularyGroupService.findById(request.vocabularyGroupId());
    if (!VocabularyGroupService.isOwner(vocabularyGroup, user)) {
      throw new IllegalAccessError("User is not an owner of the vocabulary group, userId: " + user.getId());
    }
    List<LearningSession> unfinished = learningSessionRepository.findUnfinishedTrainings(vocabularyGroup);

    if (!unfinished.isEmpty()) {
      Optional<LearningSession> first = unfinished.stream()
          .min(Comparator.comparingLong(e -> e.getCreatedAt().toEpochSecond(ZoneOffset.UTC)));

      return TrainingGenerationResponse.builder()
          .learningSessionId(first.get().getId())
          .trainingExamples(mapLearningSessionWords(first.get().getLearningWords()))
          .build();
    }

    if (vocabularyGroup.getVocabulary().getCreatedBy() == null) {
      throw new IllegalArgumentException("Owner cannot be null");
    } else if (!vocabularyGroup.getVocabulary().getCreatedBy().getId().equals(user.getId())) {
      throw new VocabularyOwnerException("User is not the owner of the vocabulary");
    }

    List<UserVocabulary> words = findWordsForTraining(vocabularyGroup);
    userVocabularyDbService.saveAll(words);

    LearningSession learningSession = LearningSession.builder()
        .user(user)
        .vocabulary(vocabularyGroup.getVocabulary())
        .learningWords(words)
        .vocabularyGroup(vocabularyGroup)
        .vocabulary(vocabularyGroup.getVocabulary())
        .createdAt(LocalDateTime.now())
        .build();
    learningSessionRepository.save(learningSession);

    log.info("Learning words: {}", learningSession.getLearningWords().stream().map(UserVocabulary::getId).toList());
    log.info("Learning session id: {}", learningSession.getId());

    List<TrainingExampleResponse> trainingExampleResponse = mapLearningSessionWords(words);

    return TrainingGenerationResponse.builder()
        .learningSessionId(learningSession.getId())
        .trainingExamples(trainingExampleResponse)
        .build();
  }

  public List<UserVocabulary> findWordsForTraining(VocabularyGroup vocabularyGroup) {
    int newWordsLimit = (int) Math.ceil(numberOfWordsPerTraining * newWordsPortion);

    List<UserVocabulary> words = new ArrayList<>();
    List<UserVocabulary> newWords = userVocabularyDbService.findNewWordsForTraining(vocabularyGroup, newWordsLimit);
    if (newWords.isEmpty()) {
      log.info("No new words found for traiining in vocabulary group wth id: {}", vocabularyGroup.getId());
    }

    if (vocabularyGroup.getType().equals(VocabularyGroupType.DEFINED_BY_USER_FROM_PREDEFINED)) {
      List<UserVocabulary> newWordsAfterTrainingCopy = newWords.stream()
          .peek(e -> e.setNextTraining(trainingService.copy(e.getNextTraining())))
          .toList();
      words.addAll(newWordsAfterTrainingCopy);
    } else if (vocabularyGroup.getType().equals(VocabularyGroupType.USER_DEFINED)) {
      words.addAll(newWords);
    } else {
      throw new IllegalArgumentException("Only user re/defined groups can be trained");
    }

    int prioritizedLimit = Math.max(0, numberOfWordsPerTraining - newWords.size());
    List<UserVocabulary> prioritizedWords = userVocabularyDbService.findPrioritizedWordsForTraining(
        vocabularyGroup, prioritizedLimit);

    if (prioritizedWords.isEmpty() && newWords.isEmpty()) {
      log.info("No words found for training in vocabulary group with id: {}", vocabularyGroup.getId());
      throw new IllegalStateException(
          "No words found for training in vocabulary group with id: " + vocabularyGroup.getId());
    }
    words.addAll(prioritizedWords);
    return words;
  }

  public TrainingGenerationResponse generateTrainingSessionForSpecificWords(
      CustomizedTrainingGenerationRequest trainingGenerationRequest) {
    VocabularyGroup vocabularyGroup = vocabularyGroupService.findById(trainingGenerationRequest.vocabularyGroupId());
    List<UserVocabulary> words = userVocabularyDbService.findWordsForCustomizedTraining(
        vocabularyGroup,
        trainingGenerationRequest.userVocabularyIds());

    LearningSession learningSession = LearningSession.builder()
        .user(vocabularyGroup.getVocabulary().getCreatedBy())
        .learningWords(words)
        .createdAt(LocalDateTime.now())
        .build();

    List<TrainingExampleResponse> trainingExampleResponse = mapLearningSessionWords(words);

    return TrainingGenerationResponse.builder()
        .learningSessionId(learningSession.getId())
        .trainingExamples(trainingExampleResponse)
        .build();
  }

  public void cancelTrainingSession(Long learningSessionId) {
    learningSessionRepository.deleteById(learningSessionId);
  }

  private List<TrainingExampleResponse> mapLearningSessionWords(List<UserVocabulary> words) {

    return words.stream()
        .map(word -> TrainingExampleResponse.builder()
            .trainingId(word.getNextTraining().getId())
            .wordTranslation(
                wordTranslationMapper.mapFromWordTranslationWithoutWordDictionaries(word.getWordTranslation()))
            .trainingExampleList(
                trainingExampleService.formatTrainingExampleForTraining(word.getNextTraining().getTrainingExamples()))
            .build())
        .toList();
  }

  @Async
  @Transactional
  public void complete(CompleteTrainingSessionRequest completeTrainingSessionRequest, SecurityContext context) {
    User requestUser = userDbService.getUser(context);
    LearningSession learningSession = learningSessionRepository
        .findById(completeTrainingSessionRequest.getLearningSessionId())
        .orElseThrow(() -> new NotFoundException("Learning session was not found"));

    User user = learningSession.getVocabularyGroup().getVocabulary().getCreatedBy();
    if (!user.getId().equals(requestUser.getId())) {
      throw new IllegalAccessError("User is not an owner of the vocabulary group, userId: " + requestUser.getId());
    }
    userStreakService.updateUserStreak(user);

    learningSession.setCompletedAt(LocalDateTime.now());

    log.info("Complete training session was called for the learning session with id: {}",
        completeTrainingSessionRequest.getLearningSessionId());

    log.info("Charging priority for the learning session with id: {}", learningSession.getId());

    Map<UserVocabulary, List<TrainingExampleResultData>> trainingResults = getTrainingResultByWord(
        completeTrainingSessionRequest.getResults(),
        learningSession.getLearningWords());
    Map<UserVocabulary, TrainingMistakeWithScore> trainingMistakes = chargePriority(trainingResults);
    log.info("Charged priority for the learning session with id: {}", learningSession.getId());

    for (UserVocabulary userVocabulary : learningSession.getLearningWords()) {
      // loading LAZY feature before running in async
      Hibernate.initialize(userVocabulary.getProblematicTrainingExamples());
      trainingService.generateNextTraining(trainingMistakes.get(userVocabulary));
    }
  }

  private Map<UserVocabulary, List<TrainingExampleResultData>> getTrainingResultByWord(
      List<CompleteTrainingTrainingIdResults> mistakes,
      List<UserVocabulary> completeTrainingWords) {
    Map<UserVocabulary, List<TrainingExampleResultData>> results = new HashMap<>();

    for (CompleteTrainingTrainingIdResults mistake : mistakes) {
      Optional<UserVocabulary> first = completeTrainingWords.stream()
          .filter(e -> e.getNextTraining().getId().equals(mistake.getTrainingId()))
          .findFirst();
      if (first.isEmpty()) {
        throw new IllegalStateException("Couldn't find training results for training_id: " + mistake.getTrainingId());
      }

      for (CompleteTrainingSessionTypesRequest trMistake : mistake.getMistakes()) {
        Optional<TrainingExample> tr = first.get().getNextTraining().getTrainingExamples().stream()
            .filter(e -> e.getId().equals(trMistake.getTrainingExampleId()))
            .findFirst();
        if (tr.isEmpty()) {
          throw new IllegalStateException(
              "Couldn't find training results for training_example_id: " + trMistake.getTrainingExampleId());
        }

        if (trMistake.getAnswers() == null) {
          trMistake
              .setAnswers(Collections.singletonList(new CompleteTrainingExample(false, false, LocalDateTime.now())));
        }

        results.computeIfAbsent(first.get(), _ -> new ArrayList<>());
        results.get(first.get()).add(
            new TrainingExampleResultData(
                tr.get(),
                trMistake.getAnswers()
                    .stream()
                    .map(TrainingExampleMistakeData::from)
                    .toList()));
      }
    }

    return results;
  }

  private Map<UserVocabulary, TrainingMistakeWithScore> chargePriority(
      Map<UserVocabulary, List<TrainingExampleResultData>> trainingResults) {
    Map<UserVocabulary, TrainingMistakeWithScore> mistakes = new HashMap<>();

    for (Map.Entry<UserVocabulary, List<TrainingExampleResultData>> entry : trainingResults.entrySet()) {
      UserVocabulary userVocabulary = entry.getKey();

      userVocabulary.getNextTraining().setCompletedAt(System.currentTimeMillis());
      trainingService.save(userVocabulary.getNextTraining());

      userVocabulary.setIsNew(false);
      userVocabulary.setLastTrainedAt(LocalDateTime.now());
      userVocabularyDbService.save(userVocabulary);

      TrainingMistakeWithScore mistake = trainingCompleteService.chargePriorityByWord(userVocabulary, entry.getValue());

      mistakes.put(userVocabulary, mistake);
    }

    return mistakes;
  }

}
