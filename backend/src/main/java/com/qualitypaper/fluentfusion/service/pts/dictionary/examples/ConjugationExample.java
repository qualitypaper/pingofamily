package com.qualitypaper.fluentfusion.service.pts.dictionary.examples;

import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordType;

import java.util.Map;

import static com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech.VERB;

public class ConjugationExample {
  private final PartOfSpeech pos;
  private final WordType wordType;

  private final ConjugationExampleFactory factory;

  public ConjugationExample(Language language, PartOfSpeech pos, WordType wordType) {
    this.wordType = wordType;
    this.pos = pos;

    this.factory = switch (language) {
      case ENGLISH -> new EnglishConjugationExample(pos);
      case SPANISH -> new SpanishConjugationExample(pos);
      case GERMAN -> new GermanConjugationExample(pos);
      default -> new EmptyConjugationExample(); // No examples for these languages
    };

  }

  public Map<String, Object> getExample() {
    if (wordType.equals(WordType.SPANISH_REFLEXIVE_VERB) && VERB.equals(pos)) {
      return SpanishConjugationExample.getSpanishSeVerbExample();
    }

    return factory.getExample();
  }
}
