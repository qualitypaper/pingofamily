package com.qualitypaper.fluentfusion.service.vocabulary.learning.types;

import com.qualitypaper.fluentfusion.config.ApplicationConfig;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.wordPrioritization.NormalizationUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record TrainingHistory(int trainingCount) {
  public static final int MIN_VALUE = 0;
  public static final int MAX_VALUE = 20;

  public TrainingHistory {
    if (trainingCount < MIN_VALUE || trainingCount > MAX_VALUE) {
      log.warn("Training count in TrainingHistory is out of bounds, clamping...");
      trainingCount = Math.clamp(trainingCount, MIN_VALUE, MAX_VALUE);
    }
  }

  public static TrainingHistory random() {
    return new TrainingHistory(ApplicationConfig.RANDOM.nextInt(MIN_VALUE, MAX_VALUE));
  }

  public double normalize() {
    return NormalizationUtil.normalize(this.trainingCount(), MIN_VALUE, MAX_VALUE);
  }
}
