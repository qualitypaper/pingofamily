package com.qualitypaper.fluentfusion.service.vocabulary.structs;

public enum TranslationDirection {
  REVERSED, INITIAL, UNKNOWN;

  public TranslationDirection reverse() {
    if (REVERSED == this) {
      return INITIAL;
    } else if (INITIAL == this) {
      return REVERSED;
    }

    return UNKNOWN;
  }
}
