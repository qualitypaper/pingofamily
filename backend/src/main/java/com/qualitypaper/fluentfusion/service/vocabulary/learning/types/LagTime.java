package com.qualitypaper.fluentfusion.service.vocabulary.learning.types;

import com.qualitypaper.fluentfusion.config.ApplicationConfig;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.wordPrioritization.NormalizationUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
public record LagTime(Duration val) {
  public static final Duration MIN_VALUE = Duration.of(1, ChronoUnit.HOURS);
  public static final Duration MAX_VALUE = Duration.of(360, ChronoUnit.HOURS);

  public LagTime {
    if (val.compareTo(MIN_VALUE) < 0 || val.compareTo(MAX_VALUE) > 0) {
      log.warn("Value in LagTime is out of bounds, clamping...");
      val = Duration.ofSeconds(Math.clamp(val.getSeconds(), MIN_VALUE.getSeconds(), MAX_VALUE.getSeconds()));
    }
  }

  public static LagTime random() {
    return new LagTime(Duration.ofHours(ApplicationConfig.RANDOM.nextLong(MIN_VALUE.toHours(), MAX_VALUE.toHours())));
  }

  public static LagTime random(Duration min) {
    return new LagTime(Duration.ofHours(ApplicationConfig.RANDOM.nextLong(min.toHours(), MAX_VALUE.toHours())));
  }

  public double normalize() {
    return NormalizationUtil.normalize(val.toHours(), MIN_VALUE.toHours(), MAX_VALUE.toHours());
  }
}
