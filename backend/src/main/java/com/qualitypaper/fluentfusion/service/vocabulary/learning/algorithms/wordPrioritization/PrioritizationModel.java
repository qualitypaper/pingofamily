package com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.wordPrioritization;


import com.qualitypaper.fluentfusion.config.ApplicationConfig;
import com.qualitypaper.fluentfusion.util.types.Pair;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.function.DoubleBinaryOperator;
import java.util.stream.DoubleStream;

@Slf4j
public class PrioritizationModel {

  @Setter
  private double fittingFactor;
  @Setter
  private double learningRate;
  @Setter
  private double relativeHalfLifeImportance;

  public PrioritizationModel(double fittingFactor, double learningRate, double relativeHalfLifeImportance) {
    this.fittingFactor = fittingFactor;
    this.learningRate = learningRate;
    this.relativeHalfLifeImportance = relativeHalfLifeImportance;
  }

  public PrioritizationModel() {
    this.fittingFactor = 0.005;
    this.relativeHalfLifeImportance = 0.001;
    this.learningRate = 0.001;
  }

  private double forgettingCurve(double lagTime, double halfLife) {
    return Math.pow(2, -lagTime / halfLife);
  }

  private double halfLife(double[] weights, double[] features) {
    if (weights.length != features.length) {
      throw new IllegalArgumentException("Incorrect length of weights: " + Arrays.toString(weights) + " and features: " + Arrays.toString(features));
    }

    double b = Math.clamp(scalarProduct(features, weights), -10, 10);
    return Math.pow(2, b);
  }

  public double[] train(double[][] x, double[] lagTimes, double[] y) {
    double[] weights = ApplicationConfig.RANDOM.doubles(x[0].length, -0.1, 0.1).toArray();

    return train(x, lagTimes, y, weights);
  }

  /*
  * @arguments
  *   x:
  *     x[@][0] -> normalized correctness score
  *     x[@][1] -> normalized experience (number of times user has seen the word before)
  *   lagTimes:
  *     lagTimes[@] -> time between the trainings
  *   y:
  *     y[@] -> results of trainings
  *   weights:
  *     weights[@] -> weights wrt. x
  *
  *
  * */
  public double[] train(double[][] x, double[] lagTimes, double[] y, double[] weights) {
    if (x.length != y.length) {
      throw new IllegalArgumentException("Unexpected prediction length " + x.length + ", expected: " + y.length);
    }


    for (int epoch = 0; epoch < 5; epoch++) {
      for (int i = 0; i < x.length; i++) {
        Pair<Double, Double> prediction = predict(x[i], lagTimes[i], weights);
        double halfLife = prediction.first(), prob = prediction.second();
        if (Double.isInfinite(halfLife) || Double.isNaN(halfLife)) {
          continue;
        }

        DoubleBinaryOperator gradient = lossFunction(lagTimes[i], halfLife, prob, y[i]);

        for (int j = 0; j < weights.length; j++) {
          weights[j] -= this.learningRate * gradient.applyAsDouble(x[i][j], weights[j]);
          weights[j] = Math.clamp(weights[j], -10, 10);
        }
      }
      log.debug("Epoch: {}, Current weights: {}", epoch, Arrays.toString(weights));
    }

    return weights;
  }

  private DoubleBinaryOperator lossFunction(double lagTime, double halfLife, double prob, double y) {
    double safeY = Math.clamp(y, 0.001, 0.999);
    double safeHalfLife = Math.max(1e-5, halfLife);
    double firstTerm = 2 * (prob - safeY) * Math.pow(Math.log(2), 2) * prob * lagTime / safeHalfLife;
    double secondTerm = 2 * Math.log(2) * this.relativeHalfLifeImportance * (safeHalfLife + lagTime / log2(safeY)) * safeHalfLife;
    double thirdTerm = 2 * this.fittingFactor;

    return (x, w) -> x * (firstTerm + secondTerm) + thirdTerm * w;
  }


  /*
  * @returns 0 -> halfLife coeff., 1 -> probability (prediction itself)
   */
  public Pair<Double, Double> predict(double[] x, double lagTime, double[] W) {
    double halfLife = halfLife(W, x);
    double prob = forgettingCurve(lagTime, halfLife);

    return new Pair<>(halfLife, prob);
  }

  private double[] multiplyVectors(double[] x, double[] weights) {
    double[] res = new double[x.length];
    for (int i = 0; i < x.length; i++) {
      res[i] = x[i] * weights[i];
    }
    return res;
  }

  private double scalarProduct(double[] a, double[] b) {
    return DoubleStream.of(multiplyVectors(a, b)).sum();
  }

  private double log2(double num) {
    return Math.log(num) / Math.log(2);
  }
}
