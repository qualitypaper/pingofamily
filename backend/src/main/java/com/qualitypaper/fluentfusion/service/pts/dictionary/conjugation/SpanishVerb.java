package com.qualitypaper.fluentfusion.service.pts.dictionary.conjugation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.HashMap;
import java.util.Map;

public class SpanishVerb {

  private static final Map<String, String> SPANISH_TENSE_MAPPING = new HashMap<>();
  private static final Map<String, String> SPANISH_PRONOUNS = new HashMap<>();

  static {
    SPANISH_TENSE_MAPPING.put("infinitive", "Infinitive");
    SPANISH_TENSE_MAPPING.put("present", "Present");
    SPANISH_TENSE_MAPPING.put("imperfect", "Imperfect");
    SPANISH_TENSE_MAPPING.put("future", "Future");
    SPANISH_TENSE_MAPPING.put("gerund", "Gerund");
    SPANISH_TENSE_MAPPING.put("conditional", "Conditional");
    SPANISH_TENSE_MAPPING.put("presentperfect", "Present Perfect");
    SPANISH_TENSE_MAPPING.put("pastperfect", "Past Perfect");
    SPANISH_TENSE_MAPPING.put("futureperfect", "Future Perfect");
    SPANISH_TENSE_MAPPING.put("conditionalperfect", "Conditional Perfect");
    SPANISH_TENSE_MAPPING.put("presentprogressive", "Present Progressive");
    SPANISH_TENSE_MAPPING.put("pastprogressive", "Past Progressive");
    SPANISH_TENSE_MAPPING.put("futureprogressive", "Future Progressive");
    SPANISH_TENSE_MAPPING.put("conditionalprogressive", "Conditional Progressive");
    SPANISH_TENSE_MAPPING.put("pastanterior", "Past Anterior");
    SPANISH_TENSE_MAPPING.put("futureanterior", "Future Anterior");
    SPANISH_TENSE_MAPPING.put("presentsubjunctive", "Present Subjunctive");
    SPANISH_TENSE_MAPPING.put("imperfectsubjunctive", "Imperfect Subjunctive");
    SPANISH_TENSE_MAPPING.put("presentperfectsubjunctive", "Present Perfect Subjunctive");
    SPANISH_TENSE_MAPPING.put("pastperfectsubjunctive", "Past Perfect Subjunctive");
    SPANISH_TENSE_MAPPING.put("futuresubjunctive", "Future Subjunctive");
    SPANISH_TENSE_MAPPING.put("futureperfectsubjunctive", "Future Perfect Subjunctive");
    SPANISH_TENSE_MAPPING.put("imperative", "Imperative");
    SPANISH_TENSE_MAPPING.put("negativeimperative", "Negative Imperative");
    SPANISH_TENSE_MAPPING.put("pastparticiplefeminineplural", "Past Participle (Feminine Plural)");
    SPANISH_TENSE_MAPPING.put("pastparticiplemasculineplural", "Past Participle (Masculine Plural)");
    SPANISH_TENSE_MAPPING.put("pastparticiplefeminine", "Past Participle (Feminine)");
    SPANISH_TENSE_MAPPING.put("pastparticiplemasculine", "Past Participle (Masculine)");

    SPANISH_PRONOUNS.put("first_plural", "yo");
    SPANISH_PRONOUNS.put("first_singular", "nosotros");
    SPANISH_PRONOUNS.put("second_singular", "irías");
    SPANISH_PRONOUNS.put("second_plural", "vosotros");
    SPANISH_PRONOUNS.put("third_singular", "él/ella/Ud.");
    SPANISH_PRONOUNS.put("third_plural", "ellos/ellas/Uds.");
  }

  public static Map<String, Map<String, String>> parseSpanishConjugations(ArrayNode conjugations) {
    Map<String, Map<String, String>> conciseConjugations = new HashMap<>();

    for (JsonNode entry : conjugations) {
      JsonNode partOfSpeech = entry.get("partofspeech");
      String originalTense = partOfSpeech.get("tense").asText();

      // Skip certain tenses
      if ("presentparticiple".equals(originalTense) || "pastparticiple".equals(originalTense) || "infinitive".equals(originalTense)) {
        continue;
      }

      String tense = SPANISH_TENSE_MAPPING.getOrDefault(originalTense, "Unknown Tense");
      String person = partOfSpeech.has("person") ? partOfSpeech.get("person").asText() : "empty";
      String number = partOfSpeech.has("number") ? partOfSpeech.get("number").asText() : "empty";
      String verbForm = entry.get("surfaceform").asText();

      if (!conciseConjugations.containsKey(tense)) {
        conciseConjugations.put(tense, new HashMap<>());
      }

      String tempKey = person + "_" + number;
      String key = SPANISH_PRONOUNS.getOrDefault(tempKey, tempKey.contains("empty") ? "" : tempKey);

      conciseConjugations.get(tense).put(key, verbForm);
    }

    return conciseConjugations;
  }
}
