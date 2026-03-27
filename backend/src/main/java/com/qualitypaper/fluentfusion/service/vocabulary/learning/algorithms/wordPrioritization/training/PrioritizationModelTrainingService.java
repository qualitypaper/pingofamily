package com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.wordPrioritization.training;

import com.qualitypaper.fluentfusion.buffers.Weights;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.wordPrioritization.PrioritizationModel;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.wordPrioritization.dataset.DatasetParser;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.wordPrioritization.dataset.NormalizedDataset;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class PrioritizationModelTrainingService {

  private static final Logger log = LoggerFactory.getLogger(PrioritizationModelTrainingService.class);
  private final PrioritizationModel prioritizationModel;

  public Weights train(String datasetFilePath) {
    NormalizedDataset normalizedDataset = DatasetParser.fromCsv(datasetFilePath);
    log.info("Parsed and normalized dataset. Starting training...");

    double[] train = prioritizationModel.train(normalizedDataset.variables(), normalizedDataset.lagTimes(), normalizedDataset.targets());
    log.info("Weights after model training: {}", Arrays.toString(train));

    return Weights.newBuilder()
            .setCorrectnessWeight(train[0])
            .setExperienceWeight(train[1])
            .build();
  }

}
