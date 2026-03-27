package com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.trainingTypeScore;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TrainingType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ScoreAlgorithm {

  private static final double MAX_SCORE = 10.0;
  private static final double MIN_SCORE = 0.0;

  private static final double DECAY_RATE = 0.5;

  private static final double RECENCY_WEIGHT = 0.25;
  private static final double PENALTY_WEIGHT = 0.15;
  private static final double CORRECTNESS_WEIGHT = 0.4;
  private static final double DIFFICULTY_WEIGHT = 0.2;
  private final List<TrainingResult> wordHistory;
  private final TrainingType trainingType;
  private final double freq;
  private final int trainingCount;
  private final Supplier<Double> correctnessRateSupplier;
  private final long typeTrainingCount;

  public ScoreAlgorithm(List<TrainingResult> wordHistory, TrainingType trainingType, int recentTrainingCount) {
    if (wordHistory == null || wordHistory.isEmpty()) {
      throw new IllegalArgumentException("Recent type trainings must not be null or empty");
    } else if (trainingType == null) {
      throw new IllegalArgumentException("Training type must not be null");
    }
    this.wordHistory = wordHistory.stream()
            .sorted(Comparator.comparingLong(e -> e.trainingTime().toEpochSecond(ZoneOffset.UTC))).toList();
    this.trainingType = trainingType;
    this.correctnessRateSupplier = this::defaultCorrectnessRateSupplier;
    this.trainingCount = Math.min(recentTrainingCount, 15);
    this.typeTrainingCount = wordHistory.stream().filter(e -> e.trainingType().equals(trainingType)).limit(15).count();
    this.freq = getFrequency();
  }

  public double calculateScore() {
    double recencyScore = calculateRecencyScore();
    double correctnessScore = calculateCorrectnessScore();
    double difficultyScore = calculateDifficultyScore();
    double penalty = calculatePenalty();

    double score = (recencyScore * RECENCY_WEIGHT) +
            (correctnessScore * CORRECTNESS_WEIGHT) +
            (difficultyScore * DIFFICULTY_WEIGHT) -
            (penalty * PENALTY_WEIGHT);

    return Math.clamp(score, MIN_SCORE, MAX_SCORE);
  }

  private double defaultCorrectnessRateSupplier() {
    // if this type wasn't trained -> rely on the difficulty score
    if (wordHistory.isEmpty()) {
      return calculateAdjustedDifficulty();
    }
    double weightedSum = 0.0;
    double weightSum = 0.0;

    for (TrainingResult result : wordHistory) {
      double timeWeight = calculateTimeWeight(result.trainingTime());
      double typeWeight = result.trainingType().equals(trainingType) ? 1.0 : 0.4;

      weightedSum += result.score().normalize() * timeWeight * typeWeight;
      weightSum += timeWeight * typeWeight;
    }

    return weightSum > 0 ? weightedSum / weightSum : 0.5; // Default to neutral if no data
  }

  private double calculateTimeWeight(LocalDateTime trainingTime) {
    double daysAgo = Duration.between(trainingTime, LocalDateTime.now()).toHours()/24.0;
    return Math.exp(-0.1 * daysAgo); // Exponential decay of weight over time
  }

  /**
   * @apiNote function: 1-exp(-0.5x*(o-1)),
   * where o -> trainingCount
   **/
  private double calculatePenalty() {
    // Soft penalty that plateaus
    return 1 - Math.exp(-0.5 * freq * (trainingCount - 1));
  }

  private double getFrequency() {
    Duration lagTime = Duration.between(wordHistory.getFirst().trainingTime(), wordHistory.getLast().trainingTime());

    return this.typeTrainingCount / Math.max(lagTime.toHours() / 24.0, 1.0);
  }

  /**
   * <p>
   * All the exponents and rates are chosen manually by drawing the function
   * It prioritizes difficulty trainings, exclusively when the word was trained
   * enough number of times with
   * a high correctness rate.
   * </p>
   *
   * <p>
   * The current approximate trainings number with correctness rate of >=0.8 is 5
   * </p>
   *
   * @apiNote The function:
   * <b>d\left(x\right)=\frac{1}{1+e^{\left(\left(1-0.3T\cdot c\right)\cdot x\right)}}}</b>
   * where:
   * T - overall number of training for this word (not only for this
   * training type)
   * c - correctness rate of the word
   */
  private double calculateDifficultyScore() {
    double adjustedDifficulty = calculateAdjustedDifficulty();
    double correctnessFactor = correctnessRateSupplier.get();

    if (trainingCount < 5) {
      return 1 - adjustedDifficulty;
    }

    // Normalized to 0-1 range where 0.5 is neutral
    return sigmoid(
            (adjustedDifficulty * 2) *
                    (0.2 * trainingCount * correctnessFactor - 0.7)
    );
  }

  private double calculateAdjustedDifficulty() {
    // Base difficulty from the training type
    double baseDifficulty = trainingType.getDifficulty();

    // Number of trainings acts as experience metric
    int experience = wordHistory.size();

    // Gradually introduce full difficulty over first 10 trainings
    double easingFactor = 1.0;
    if (experience < 10) {
      // Ease in the difficulty - starts at 0.3 and reaches 1.0 by 10th training
      easingFactor = 0.5 + (0.5 * experience / 10.0);
    }

    return baseDifficulty * easingFactor;
  }

  private double calculateCorrectnessScore() {
    return sigmoid(10 * (correctnessRateSupplier.get() - 0.5));
  }


  private LocalDateTime getLastRelevantTrainingTime() {
    LocalDateTime lastOverallTrainingTime = wordHistory.getLast().trainingTime();
    Optional<LocalDateTime> lastTypeTraining = wordHistory.stream()
            .filter(e -> e.trainingType().equals(trainingType))
            .map(TrainingResult::trainingTime)
            .max(Comparator.naturalOrder());

    return lastTypeTraining.orElse(lastOverallTrainingTime);

  }

  private double calculateRecencyScore() {
    Duration diff = Duration.between(LocalDateTime.now(), getLastRelevantTrainingTime());

    return Math.pow(1.5, DECAY_RATE * diff.toHours() / 24.0);
  }

  private double sigmoid(double x) {
    return 1 / (1 + Math.exp(-x));
  }
}
