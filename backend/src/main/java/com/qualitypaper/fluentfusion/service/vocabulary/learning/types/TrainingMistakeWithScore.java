package com.qualitypaper.fluentfusion.service.vocabulary.learning.types;

import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingScore.TrainingScore;

import java.util.List;

// trainingScore -> val for the whole training of the word
// mean of all trainingExamplesScores
public record TrainingMistakeWithScore(UserVocabulary userVocabulary,
                                       List<TrainingMistake> trainingMistakes,
                                       TrainingScore trainingScore) {

  public double getAverageDifficulty() {
    return trainingMistakes.stream()
            .mapToDouble(e -> e.trainingExampleData().trainingExample().getTrainingExampleData().getTrainingType().getDifficulty())
            .average()
            .orElse(0.3);
  }
}
