package com.qualitypaper.fluentfusion.service.pts.dictionary.conjugation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.HashMap;
import java.util.Map;

public class EnglishVerb {

  private static final Map<String, String> TENSE_MAPPING = new HashMap<>();
  private static final Map<String, Object> ENGLISH_PRONOUNS = new HashMap<>();

  static {
    TENSE_MAPPING.put("infinitive", "Infinitive");
    TENSE_MAPPING.put("presentparticiple", "Present Participle");
    TENSE_MAPPING.put("present", "Present Simple");
    TENSE_MAPPING.put("past", "Past Simple");
    TENSE_MAPPING.put("future", "Future Simple");
    TENSE_MAPPING.put("presentperfect", "Present Perfect");
    TENSE_MAPPING.put("pastperfect", "Past Perfect");
    TENSE_MAPPING.put("futureperfect", "Future Perfect");
    TENSE_MAPPING.put("presentprogressive", "Present Continuous");
    TENSE_MAPPING.put("pastprogressive", "Past Continuous");
    TENSE_MAPPING.put("futureprogressive", "Future Continuous");
    TENSE_MAPPING.put("presentperfectprogressive", "Present Perfect Continuous");
    TENSE_MAPPING.put("pastperfectprogressive", "Past Perfect Continuous");
    TENSE_MAPPING.put("futureperfectprogressive", "Future Perfect Continuous");
    TENSE_MAPPING.put("conditional", "Conditional");
    TENSE_MAPPING.put("pastconditional", "Past Conditional");
    TENSE_MAPPING.put("conditionalprogressive", "Conditional Continuous");
    TENSE_MAPPING.put("pastconditionalprogressive", "Past Conditional Continuous");
    TENSE_MAPPING.put("imperative", "Imperative");
    TENSE_MAPPING.put("imperfect", "Imperfect");
    TENSE_MAPPING.put("imperfectprogressive", "Imperfect Continuous");

    ENGLISH_PRONOUNS.put("first", "I");
    ENGLISH_PRONOUNS.put("second", "you");
    Map<String, String> thirdPersonPronouns = new HashMap<>();
    thirdPersonPronouns.put("singular", "he/she/it");
    thirdPersonPronouns.put("plural", "they");
    ENGLISH_PRONOUNS.put("third", thirdPersonPronouns);
  }

  @SuppressWarnings("unchecked")
  public static Map<String, Map<String, String>> parseEnglishConjugations(ArrayNode conjugations) {
    Map<String, Map<String, String>> conciseConjugations = new HashMap<>();

    for (JsonNode entry : conjugations) {
      JsonNode partOfSpeech = entry.get("partofspeech");
      String originalTense = partOfSpeech.get("tense").asText();

      if ("presentparticiple".equals(originalTense) || "pastparticiple".equals(originalTense) || "infinitive".equals(originalTense)) {
        continue;
      }

      String tense = TENSE_MAPPING.getOrDefault(originalTense, "Unknown Tense");
      String person = partOfSpeech.has("person") ? partOfSpeech.get("person").asText() : "empty";
      String number = partOfSpeech.has("number") ? partOfSpeech.get("number").asText() : "empty";
      String verbForm = entry.get("surfaceform").asText();

      // Map pronouns to English equivalents
      if (ENGLISH_PRONOUNS.containsKey(person)) {
        Object pronounMapping = ENGLISH_PRONOUNS.get(person);
        if (pronounMapping instanceof Map) {
          Map<String, String> pronounMap = (Map<String, String>) pronounMapping;
          person = pronounMap.getOrDefault(number, "empty");
        } else {
          person = (String) pronounMapping;
        }
      }

      if (!conciseConjugations.containsKey(tense)) {
        conciseConjugations.put(tense, new HashMap<>());
      }

      conciseConjugations.get(tense).put(person, verbForm);
    }

    return conciseConjugations;
  }
}
