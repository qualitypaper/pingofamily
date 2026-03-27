package com.qualitypaper.fluentfusion.service.pts.dictionary.examples;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;

import java.util.HashMap;
import java.util.Map;

record EnglishConjugationExample(PartOfSpeech pos) implements ConjugationExampleFactory {


  private Map<String, Object> getEnglishVerbExample() {
    Map<String, Object> verbExample = new HashMap<>();
    verbExample.put("Present", getEnglishPresentConjugation());
    verbExample.put("Past simple", getEnglishPastSimpleConjugation());
    verbExample.put("Present continuous", getEnglishPresentContinuousConjugation());
    verbExample.put("Present perfect", getEnglishPresentPerfectConjugation());
    verbExample.put("Future", getEnglishFutureConjugation());
    verbExample.put("Future perfect", getEnglishFuturePerfectConjugation());
    verbExample.put("Past continuous", getEnglishPastContinuousConjugation());
    verbExample.put("Past perfect", getEnglishPastPerfectConjugation());
    verbExample.put("Present perfect continuous", getEnglishPresentPerfectContinuousConjugation());
    verbExample.put("Past perfect continuous", getEnglishPastPerfectContinuousConjugation());
    verbExample.put("Future continuous", getEnglishFutureContinuousConjugation());
    verbExample.put("Future perfect continuous", getEnglishFuturePerfectContinuousConjugation());
    return verbExample;
  }

  private Map<String, Object> getEnglishAdjectiveExample() {
    Map<String, Object> adjectiveExample = new HashMap<>();
    adjectiveExample.put("word", "big");
    adjectiveExample.put("mappings", getEnglishAdjectiveMappings());
    return adjectiveExample;
  }

  private Map<String, Object> getEnglishNounExample() {
    Map<String, Object> nounExample = new HashMap<>();
    nounExample.put("infinitive", "book");
    nounExample.put("plural", "books");
    nounExample.put("possessive", "book's");
    nounExample.put("plural_possessive", "books'");
    return nounExample;
  }


  @Override
  public Map<String, Object> getExample() {

    return switch (pos) {
      case NOUN -> getEnglishNounExample();
      case VERB -> getEnglishVerbExample();
      case ADJECTIVE -> getEnglishAdjectiveExample();
      default -> new HashMap<>();
    };
  }


  // Helper methods for English examples
  private Map<String, String> getEnglishAdjectiveMappings() {
    Map<String, String> mappings = new HashMap<>();
    mappings.put("comparative", "bigger");
    mappings.put("superlative", "biggest");
    return mappings;
  }

  private Map<String, String> getEnglishPresentConjugation() {
    Map<String, String> conjugation = new HashMap<>();
    conjugation.put("I", "work");
    conjugation.put("he/she/it", "works");
    conjugation.put("we", "work");
    conjugation.put("you", "work");
    conjugation.put("they", "work");
    return conjugation;
  }

  private Map<String, String> getEnglishPastSimpleConjugation() {
    Map<String, String> conjugation = new HashMap<>();
    conjugation.put("I", "worked");
    conjugation.put("he/she/it", "worked");
    conjugation.put("we", "worked");
    conjugation.put("you", "worked");
    conjugation.put("they", "worked");
    return conjugation;
  }

  private Map<String, String> getEnglishPresentContinuousConjugation() {
    Map<String, String> conjugation = new HashMap<>();
    conjugation.put("I", "am working");
    conjugation.put("he/she/it", "is working");
    conjugation.put("we", "are working");
    conjugation.put("you", "are working");
    conjugation.put("they", "are working");
    return conjugation;
  }

  private Map<String, String> getEnglishPresentPerfectConjugation() {
    Map<String, String> conjugation = new HashMap<>();
    conjugation.put("I", "have worked");
    conjugation.put("he/she/it", "has worked");
    conjugation.put("we", "have worked");
    conjugation.put("you", "have worked");
    conjugation.put("they", "have worked");
    return conjugation;
  }

  private Map<String, String> getEnglishFutureConjugation() {
    Map<String, String> conjugation = new HashMap<>();
    conjugation.put("I", "will work");
    conjugation.put("he/she/it", "will work");
    conjugation.put("we", "will work");
    conjugation.put("you", "will work");
    conjugation.put("they", "will work");
    return conjugation;
  }

  private Map<String, String> getEnglishFuturePerfectConjugation() {
    Map<String, String> conjugation = new HashMap<>();
    conjugation.put("I", "will have worked");
    conjugation.put("he/she/it", "will have worked");
    conjugation.put("we", "will have worked");
    conjugation.put("you", "will have worked");
    conjugation.put("they", "will have worked");
    return conjugation;
  }

  private Map<String, String> getEnglishPastContinuousConjugation() {
    Map<String, String> conjugation = new HashMap<>();
    conjugation.put("I", "was working");
    conjugation.put("he/she/it", "was working");
    conjugation.put("we", "were working");
    conjugation.put("you", "were working");
    conjugation.put("they", "were working");
    return conjugation;
  }

  private Map<String, String> getEnglishPastPerfectConjugation() {
    Map<String, String> conjugation = new HashMap<>();
    conjugation.put("I", "had worked");
    conjugation.put("he/she/it", "had worked");
    conjugation.put("we", "had worked");
    conjugation.put("you", "had worked");
    conjugation.put("they", "had worked");
    return conjugation;
  }

  private Map<String, String> getEnglishPresentPerfectContinuousConjugation() {
    Map<String, String> conjugation = new HashMap<>();
    conjugation.put("I", "have been working");
    conjugation.put("he/she/it", "has been working");
    conjugation.put("we", "have been working");
    conjugation.put("you", "have been working");
    conjugation.put("they", "have been working");
    return conjugation;
  }

  private Map<String, String> getEnglishPastPerfectContinuousConjugation() {
    Map<String, String> conjugation = new HashMap<>();
    conjugation.put("I", "had been working");
    conjugation.put("he/she/it", "had been working");
    conjugation.put("we", "had been working");
    conjugation.put("you", "had been working");
    conjugation.put("they", "had been working");
    return conjugation;
  }

  private Map<String, String> getEnglishFutureContinuousConjugation() {
    Map<String, String> conjugation = new HashMap<>();
    conjugation.put("I", "will be working");
    conjugation.put("he/she/it", "will be working");
    conjugation.put("we", "will be working");
    conjugation.put("you", "will be working");
    conjugation.put("they", "will be working");
    return conjugation;
  }

  private Map<String, String> getEnglishFuturePerfectContinuousConjugation() {
    Map<String, String> conjugation = new HashMap<>();
    conjugation.put("I", "will have been working");
    conjugation.put("he/she/it", "will have been working");
    conjugation.put("we", "will have been working");
    conjugation.put("you", "will have been working");
    conjugation.put("they", "will have been working");
    return conjugation;
  }


}
