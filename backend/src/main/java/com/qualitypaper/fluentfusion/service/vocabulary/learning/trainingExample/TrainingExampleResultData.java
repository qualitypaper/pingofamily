package com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExample;

import java.util.List;

public record TrainingExampleResultData(TrainingExample trainingExample,
                                        List<TrainingExampleMistakeData> mistakeData) {
}
