package com.qualitypaper.fluentfusion.service.pts.dictionary.examples;

import java.util.Collections;
import java.util.Map;

public record EmptyConjugationExample() implements ConjugationExampleFactory {

  @Override
  public Map<String, Object> getExample() {
    return Collections.emptyMap();
  }
}
