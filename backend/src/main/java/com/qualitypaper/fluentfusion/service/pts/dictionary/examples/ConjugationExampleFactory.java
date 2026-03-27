package com.qualitypaper.fluentfusion.service.pts.dictionary.examples;

import java.util.Map;

public sealed interface ConjugationExampleFactory
        permits EmptyConjugationExample, EnglishConjugationExample, GermanConjugationExample, SpanishConjugationExample {
  Map<String, Object> getExample();
}
