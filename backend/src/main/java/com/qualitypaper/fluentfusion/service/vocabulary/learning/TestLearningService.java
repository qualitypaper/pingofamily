package com.qualitypaper.fluentfusion.service.vocabulary.learning;

import com.qualitypaper.fluentfusion.controller.dto.request.*;
import com.qualitypaper.fluentfusion.model.user.Role;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroup;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.LearningSession;
import com.qualitypaper.fluentfusion.repository.LearningSessionRepository;
import com.qualitypaper.fluentfusion.service.db.queries.resultTypes.GetWordsForTraining;
import com.qualitypaper.fluentfusion.service.user.UserDbService;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.TrainingGenerationType;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.UserVocabularyDbService;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabularyGroup.VocabularyGroupService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestLearningService {

  private final UserDbService userDbService;
  private final LearningSessionRepository learningSessionRepository;

  private static final Random random = new Random();
  private final VocabularyGroupService vocabularyGroupService;
  private final UserVocabularyDbService userVocabularyDbService;

  public CompleteTrainingSessionRequest generateRandomTrainingResults(CustomizedTrainingGenerationRequest trainingGenerationRequest) {
    return generateThroughTrainingGenerationType(SecurityContextHolder.getContext(), TrainingGenerationType.RANDOM, trainingGenerationRequest);
  }

  public CompleteTrainingSessionRequest generateAllCorrectTrainingResults(CustomizedTrainingGenerationRequest trainingGenerationRequest) {
    return generateThroughTrainingGenerationType(SecurityContextHolder.getContext(), TrainingGenerationType.ALL_CORRECT, trainingGenerationRequest);
  }

  public CompleteTrainingSessionRequest generateAllWrongTrainingResults(CustomizedTrainingGenerationRequest trainingGenerationRequest) {
    return generateThroughTrainingGenerationType(SecurityContextHolder.getContext(), TrainingGenerationType.ALL_WRONG, trainingGenerationRequest);
  }

  @NotNull
  private CompleteTrainingSessionRequest generateThroughTrainingGenerationType(SecurityContext securityContext, TrainingGenerationType trainingGenerationType, CustomizedTrainingGenerationRequest trainingGenerationRequest) {
    User user = userDbService.getUser(securityContext);
    if (!user.getRole().equals(Role.ROLE_ADMIN)) {
      throw new IllegalCallerException("Only admin can generate training results");
    }
    VocabularyGroup vocabularyGroup = vocabularyGroupService.findById(trainingGenerationRequest.vocabularyGroupId());

    List<UserVocabulary> words = userVocabularyDbService.findWordsForCustomizedTraining(vocabularyGroup, trainingGenerationRequest.userVocabularyIds());

    LearningSession learningSession = LearningSession.builder()
            .learningWords(words)
            .vocabularyGroup(vocabularyGroup)
            .user(vocabularyGroup.getVocabulary().getCreatedBy())
            .createdAt(LocalDateTime.now())
            .build();
    learningSessionRepository.save(learningSession);

    List<CompleteTrainingTrainingIdResults> mistakes = new ArrayList<>();

    Collector<GetWordsForTraining, ?, Map<Long, List<GetWordsForTraining>>> collector =
            Collectors.groupingBy(GetWordsForTraining::getUserVocabularyId);

//        for (Map.Entry<Long, List<GetWordsForTraining>> entry : all.stream().collect(collector).entrySet()) {
//            long trainingId = -1;
//            List<CompleteTrainingSessionTypesRequest> results = new ArrayList<>();
//
//            for (GetWordsForTraining word : entry.getValue()) {
//                trainingId = word.trainingId;
//                CompleteTrainingSessionTypesRequest result = generateByGenerationType(trainingGenerationType, word.trainingExampleId);
//                results.add(result);
//            }
//            if (trainingId == -1) continue;
//
//            mistakes.add(new CompleteTrainingTrainingIdResults(trainingId, results));
//        }
//
//        return new CompleteTrainingSessionRequest(learningSession.getId(), mistakes);
    return null;
  }

  private CompleteTrainingSessionTypesRequest generateByGenerationType(TrainingGenerationType trainingGenerationType, long trainingExampleId) {

    Map<String, Object> map = new HashMap<>();
    switch (trainingGenerationType) {
      case ALL_WRONG -> {
        map.put("hint", true);
        map.put("mistakeCount", 3);
        map.put("skipped", true);
      }
      case ALL_CORRECT -> {
        map.put("hint", false);
        map.put("mistakeCount", 0);
        map.put("skipped", false);
      }
      default -> {
        map.put("hint", random.nextBoolean());
        map.put("skipped", random.nextBoolean());
        if (map.get("skipped").equals(true)) {
          map.put("mistakeCount", random.nextInt(3));
        } else {
          map.put("mistakeCount", 0);
        }
      }
    }

    return CompleteTrainingSessionTypesRequest.builder()
            .trainingExampleId(trainingExampleId)
            .answers(Collections.singletonList(new CompleteTrainingExample((Boolean) map.get("hint"), (Boolean) map.get("skipped"), LocalDateTime.now())))
            .build();
  }
}
