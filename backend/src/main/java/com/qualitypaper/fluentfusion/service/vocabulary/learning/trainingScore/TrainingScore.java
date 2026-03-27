package com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingScore;

import com.qualitypaper.fluentfusion.config.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record TrainingScore(double val) {
  public static final double MAX_VALUE = 10.0;
  public static final double MIN_VALUE = 0.0;

  public TrainingScore {
    if (val < MIN_VALUE || val > MAX_VALUE) {
      log.error("Value in TrainingScore is not within bounds, clamping");
      val = Math.clamp(val, MIN_VALUE, MAX_VALUE);
    }
  }

  public static TrainingScore random() {
    return new TrainingScore(ApplicationConfig.RANDOM.nextDouble(MIN_VALUE, MAX_VALUE));
  }

  public boolean isMistake() {
    return this.val < 7;
  }

  // using logminmax normalization
  public double normalize() {
    return (Math.log1p(this.val()) - Math.log1p(MIN_VALUE))/(Math.log1p(MAX_VALUE) - Math.log1p(MIN_VALUE));
  }
}
