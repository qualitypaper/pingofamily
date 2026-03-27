package com.qualitypaper.fluentfusion.service.vocabulary.learning.types;

import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample.TrainingExampleResultData;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingScore.TrainingScore;

// trainingExampleScore for the stored training example
public record TrainingMistake(TrainingExampleResultData trainingExampleData,
                              TrainingScore trainingExampleScore) {
}
