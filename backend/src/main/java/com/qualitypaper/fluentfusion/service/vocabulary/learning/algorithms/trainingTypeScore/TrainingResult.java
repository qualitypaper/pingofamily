package com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.trainingTypeScore;

import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabularyStatistics;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TrainingType;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExample;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingScore.TrainingScore;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.TrainingMistake;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.TrainingMistakeWithScore;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

public record TrainingResult(
        TrainingScore score,
        double averageDifficulty,
        Duration lagTime,
        LocalDateTime trainingTime,
        TrainingType trainingType
) {

  public static List<TrainingResult> from(UserVocabularyStatistics statistics, Function<TrainingExample, TrainingScore> scoreGenerator) {
    if (statistics == null) {
      throw new IllegalArgumentException("UserVocabularyStatistics must not be null");
    }
    return statistics.getTraining().getTrainingExamples().stream()
            .map(e -> new TrainingResult(
                    scoreGenerator.apply(e),
                    statistics.getAverageDifficulty(),
                    statistics.getTrainingTimeDifference(),
                    statistics.getTrainingTime(),
                    e.getTrainingExampleData().getTrainingType()
            ))
            .toList();
  }

  public static List<TrainingResult> from(TrainingMistakeWithScore mistake, Duration lagTime, LocalDateTime trainingTime, Function<TrainingMistake, TrainingScore> scoreGenerator) {
    if (mistake == null) {
      throw new IllegalArgumentException("TrainingMistakeWithPercentage must not be null");
    }
    return mistake.trainingMistakes().stream()
            .map(e -> new TrainingResult(
                    scoreGenerator.apply(e),
                    mistake.getAverageDifficulty(),
                    lagTime,
                    trainingTime,
                    e.trainingExampleData().trainingExample().getTrainingExampleData().getTrainingType()
            ))
            .toList();
  }
}
