package com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.wordPrioritization;

public class NormalizationUtil {


  public static void normalize(double[] values, double min, double max) {
    for (int i = 0; i < values.length; i++) {
      values[i] = normalize(values[i], min, max);
    }
  }

  public static double normalize(double val, double min, double max) {
    double value = Math.clamp(val, min, max);
    return (Math.log1p(value) - Math.log1p(min)) / (Math.log1p(max) - Math.log1p(min));
  }

}
