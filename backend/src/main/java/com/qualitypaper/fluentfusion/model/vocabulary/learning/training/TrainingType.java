package com.qualitypaper.fluentfusion.model.vocabulary.learning.training;

public enum TrainingType {
  TRANSLATION,
  COMPLETE_EMPTY_SPACES,
  PHRASE_CONSTRUCTION,
  AUDIO,
  // reversed trainings
  PHRASE_CONSTRUCTION_REVERSED,
  // hard trainings
  SENTENCE_AUDIO,
  SENTENCE_TYPE;

  // diff -> (0; 1) excluding the boundaries
  public double getDifficulty() {
    return switch (this) {
      case COMPLETE_EMPTY_SPACES -> 0.75;
      case PHRASE_CONSTRUCTION, PHRASE_CONSTRUCTION_REVERSED -> 0.5;
      case TRANSLATION -> 0.2;
      case AUDIO -> 0.3;
      case SENTENCE_AUDIO, SENTENCE_TYPE -> 0.9;
      default -> throw new IllegalStateException("Unexpected value: " + this);
    };
  }

  public TrainingType tryToReversed() {
    try {
      return TrainingType.valueOf(this.name() + "_REVERSED");
    } catch (IllegalArgumentException e) {
      return this;
    }
  }

  public TrainingType tryToDefault() {
    if (!this.name().contains("_REVERSED")) {
      return this;
    }

    return TrainingType.valueOf(this.name().substring(0, this.name().indexOf("_REVERSED")));
  }
}
