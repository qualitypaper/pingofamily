package com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.statistics;

import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabularyStatistics;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.Training;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TrainingType;
import com.qualitypaper.fluentfusion.repository.UserVocabularyStatisticsRepository;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.TrainingMistakeWithScore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserVocabularyStatisticsService {

  private final UserVocabularyStatisticsRepository userVocabularyStatisticsRepository;

  public UserVocabularyStatistics save(UserVocabularyStatistics userVocabularyStatistics) {
    return userVocabularyStatisticsRepository.save(userVocabularyStatistics);
  }

  public Optional<UserVocabularyStatistics> findByTraining(Training training) {
    return userVocabularyStatisticsRepository.findByTraining(training);
  }


  @Transactional
  public void createUserVocabularyStatistics(TrainingMistakeWithScore trainingMistakeWithScore) {
    UserVocabularyStatistics userVocabularyStatistics = UserVocabularyStatistics.builder()
            .userVocabulary(trainingMistakeWithScore.userVocabulary())
            .training(trainingMistakeWithScore.userVocabulary().getNextTraining())
            .averageDifficulty(trainingMistakeWithScore.getAverageDifficulty())
            .trainingScore(trainingMistakeWithScore.trainingScore().val())
            .trainingTime(trainingMistakeWithScore.userVocabulary().getLastTrainedAt())
            .trainingTimeDifference(
                    Duration.ofMillis(trainingMistakeWithScore.userVocabulary().getNextTraining().getCompletedAt()
                            - trainingMistakeWithScore.userVocabulary().getNextTraining().getCreatedAt())
            )
            .build();

    userVocabularyStatisticsRepository.save(userVocabularyStatistics);
  }

  public List<UserVocabularyStatistics> findWordTrainingsAfter(UserVocabulary userVocabulary, LocalDateTime localDateTime) {
    return userVocabularyStatisticsRepository.findAllByUserVocabularyAndTrainingTimeAfter(userVocabulary, localDateTime);
  }

  public long findWordTrainingsByTrainingTypeAfter(long userVocabularyId, TrainingType trainingType, LocalDateTime date) {
    return userVocabularyStatisticsRepository.countByTrainingTypeAndTrainingTimeAfter(userVocabularyId, trainingType.name(), date);
  }

  public long findWordTrainingsByTrainingTypeAfter(UserVocabulary userVocabulary, TrainingType trainingType, LocalDateTime date) {
    return userVocabularyStatisticsRepository.countByTrainingTypeAndTrainingTimeAfter(userVocabulary.getId(), trainingType.name(), date);
  }
}
