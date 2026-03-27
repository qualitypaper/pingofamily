package com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingScore;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleStatistics;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample.TrainingExampleMistakeData;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.TrainingMistake;

import java.util.List;

public interface TrainingScoreFactory {
  TrainingScore getTrainingScoreAndConvert(List<TrainingExampleStatistics> statistics);

  TrainingScore getTrainingScore(List<TrainingExampleMistakeData> result);
  TrainingScore getOverallTrainingScore(List<TrainingMistake> result);
}
