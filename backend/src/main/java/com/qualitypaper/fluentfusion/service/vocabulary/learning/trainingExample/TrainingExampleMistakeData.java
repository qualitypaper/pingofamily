package com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample;

import com.qualitypaper.fluentfusion.controller.dto.request.CompleteTrainingExample;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleStatistics;

import java.time.LocalDateTime;

public record TrainingExampleMistakeData(boolean hint, boolean skipped, LocalDateTime timestamp) {
  public static TrainingExampleMistakeData from(TrainingExampleStatistics statistics) {
    return new TrainingExampleMistakeData(statistics.getHint(), statistics.getSkipped(), statistics.getCreatedAt());
  }

  public static TrainingExampleMistakeData from(CompleteTrainingExample completeTrainingExample) {
    return new TrainingExampleMistakeData(completeTrainingExample.hint(), completeTrainingExample.skipped(), completeTrainingExample.timestamp());
  }
}


