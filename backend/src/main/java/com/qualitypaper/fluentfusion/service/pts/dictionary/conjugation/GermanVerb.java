package com.qualitypaper.fluentfusion.service.pts.dictionary.conjugation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.HashMap;
import java.util.Map;

public class GermanVerb {

  private static final Map<String, String> TENSE_MAPPING = new HashMap<>();
  private static final Map<String, Map<String, String>> GERMAN_PRONOUNS = new HashMap<>();

  static {
    TENSE_MAPPING.put("infinitive", "Infinitive");
    TENSE_MAPPING.put("presentparticiple", "Present Participle");
    TENSE_MAPPING.put("pastparticiple", "Past Participle");
    TENSE_MAPPING.put("present", "Present");
    TENSE_MAPPING.put("future", "Future");
    TENSE_MAPPING.put("futureperfect", "Future Perfect");
    TENSE_MAPPING.put("presentperfect", "Present Perfect");
    TENSE_MAPPING.put("past", "Past");
    TENSE_MAPPING.put("pastperfect", "Past Perfect");
    TENSE_MAPPING.put("presentsubjunctive", "Present Subjunctive");
    TENSE_MAPPING.put("presentsubjunctive2", "Present Subjunctive 2");
    TENSE_MAPPING.put("futureperfectsubjunctive", "Future Perfect Subjunctive");
    TENSE_MAPPING.put("futureperfectsubjunctive2", "Future Perfect Subjunctive 2");
    TENSE_MAPPING.put("pastsubjunctive", "Past Subjunctive");
    TENSE_MAPPING.put("pastsubjunctive2", "Past Subjunctive 2");
    TENSE_MAPPING.put("imperative", "Imperative");

    Map<String, String> firstPerson = new HashMap<>();
    firstPerson.put("singular", "ich");
    firstPerson.put("plural", "wir");

    Map<String, String> secondPerson = new HashMap<>();
    secondPerson.put("singular", "du");
    secondPerson.put("plural", "ihr");

    Map<String, String> thirdPerson = new HashMap<>();
    thirdPerson.put("singular", "er/sie/es");
    thirdPerson.put("plural", "Sie/sie");

    GERMAN_PRONOUNS.put("first", firstPerson);
    GERMAN_PRONOUNS.put("second", secondPerson);
    GERMAN_PRONOUNS.put("third", thirdPerson);
  }

  public static Map<String, Map<String, String>> parseGermanConjugation(ArrayNode conjugations) {
    Map<String, Map<String, String>> conciseConjugations = new HashMap<>();

    for (JsonNode entry : conjugations) {
      JsonNode partOfSpeech = entry.get("partofspeech");
      String originalTense = partOfSpeech.get("tense").asText();

      if ("presentparticiple".equals(originalTense)
              || "pastparticiple".equals(originalTense)
              || "infinitive".equals(originalTense))
        continue;


      String tense = TENSE_MAPPING.getOrDefault(originalTense, "Unknown Tense");
      String person = partOfSpeech.has("person") ? partOfSpeech.get("person").asText() : "empty";
      String number = partOfSpeech.has("number") ? partOfSpeech.get("number").asText() : "empty";
      String verbForm = entry.get("surfaceform").asText();

      if (GERMAN_PRONOUNS.containsKey(person)) {
        person = GERMAN_PRONOUNS.get(person).getOrDefault(number, "empty");
      }

      conciseConjugations.computeIfAbsent(tense, _ -> new HashMap<>());
      conciseConjugations.get(tense).put(person, verbForm);
    }

    return conciseConjugations;
  }
}