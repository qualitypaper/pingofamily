package com.qualitypaper.fluentfusion.config.ai;

import com.qualitypaper.fluentfusion.buffers.Weights;
import com.qualitypaper.fluentfusion.service.serialization.WeightsSerializationService;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.wordPrioritization.training.PrioritizationModelTrainingService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class PreTrainingConfiguration {

  private static final Logger log = LoggerFactory.getLogger(PreTrainingConfiguration.class);

  private final WeightsSerializationService weightsSerializationService;
  private final PrioritizationModelTrainingService prioritizationModelTrainingService;

  @Value("${ai.dataset.default.path}")
  private String defaultDatasetFilepath;
  @Value("${ai.weights.default.path}")
  private String defaultWeightsFilepath;
  @Value("${ai.training.force-retraining}")
  private boolean forceRetraining;


  @PostConstruct
  public void init() throws IOException {
    if (forceRetraining) {
      Weights weights = prioritizationModelTrainingService.train(defaultDatasetFilepath);
      weightsSerializationService.writeWeights(weights, defaultWeightsFilepath);
    } else {
      try {
        Weights _ = WeightsSerializationService.loadWeights(defaultWeightsFilepath);
      } catch (IOException e) {
        log.error(e.getMessage(), e);
        log.info("Training model with the default dataset");

        Weights weights = prioritizationModelTrainingService.train(defaultDatasetFilepath);
        weightsSerializationService.writeWeights(weights, defaultWeightsFilepath);
      }
    }
  }
}
