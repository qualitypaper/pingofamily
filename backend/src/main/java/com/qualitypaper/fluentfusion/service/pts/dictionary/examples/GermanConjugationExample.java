package com.qualitypaper.fluentfusion.service.pts.dictionary.examples;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;

import java.util.HashMap;
import java.util.Map;

record GermanConjugationExample(PartOfSpeech pos) implements ConjugationExampleFactory {

  private Map<String, Object> getGermanNounExample() {
    Map<String, Object> nounExample = new HashMap<>();
    nounExample.put("infinitive", "das Buch");
    nounExample.put("gender", "neuter");
    nounExample.put("plural", "die Bücher");
    nounExample.put("mappings", getGermanNounMappings());
    return nounExample;
  }

  @Override
  public Map<String, Object> getExample() {

    return switch (pos) {
      case NOUN -> getGermanNounExample();
      case VERB -> getGermanVerbExample();
      case ADJECTIVE -> getGermanAdjectiveExample();
      default -> new HashMap<>();
    };
  }

  private Map<String, Object> getGermanAdjectiveExample() {
    Map<String, Object> adjectiveExample = new HashMap<>();
    adjectiveExample.put("word", "groß");
    adjectiveExample.put("mappings", getGermanAdjectiveMappings());
    return adjectiveExample;
  }

  private Map<String, Object> getGermanVerbExample() {
    Map<String, Object> verbExample = new HashMap<>();
    verbExample.put("infinitive", "lernen");
    verbExample.put("conjugations", getGermanVerbConjugations());
    return verbExample;
  }


  // Helper methods for German examples
  private Map<String, Object> getGermanNounMappings() {
    Map<String, Object> mappings = new HashMap<>();
    mappings.put("nominative", getGermanNounCaseMappings("das Buch", "die Bücher"));
    mappings.put("accusative", getGermanNounCaseMappings("das Buch", "die Bücher"));
    mappings.put("dative", getGermanNounCaseMappings("dem Buch", "den Büchern"));
    mappings.put("genitive", getGermanNounCaseMappings("des Buches", "der Bücher"));
    return mappings;
  }

  private Map<String, String> getGermanNounCaseMappings(String singular, String plural) {
    Map<String, String> caseMappings = new HashMap<>();
    caseMappings.put("singular", singular);
    caseMappings.put("plural", plural);
    return caseMappings;
  }

  private Map<String, String> getGermanAdjectiveMappings() {
    Map<String, String> mappings = new HashMap<>();
    mappings.put("comparative", "größer");
    mappings.put("superlative", "am größten");
    return mappings;
  }

  private Map<String, Object> getGermanVerbConjugations() {
    Map<String, Object> conjugations = new HashMap<>();
    conjugations.put("Präsens", getGermanVerbTenseConjugations("lerne", "lernst", "lernt", "lernen", "lernt", "lernen"));
    conjugations.put("Präteritum", getGermanVerbTenseConjugations("lernte", "lerntest", "lernte", "lernten", "lerntet", "lernten"));
    conjugations.put("Futur 1", getGermanVerbTenseConjugations("werde lernen", "wirst lernen", "wird lernen", "werden lernen", "werdet lernen", "werden lernen"));
    conjugations.put("Perfekt", getGermanVerbTenseConjugations("habe gelernt", "hast gelernt", "hat gelernt", "haben gelernt", "habt gelernt", "haben gelernt"));
    conjugations.put("Plusquamperfekt", getGermanVerbTenseConjugations("hatte gelernt", "hattest gelernt", "hatte gelernt", "hatten gelernt", "hattet gelernt", "hatten gelernt"));
    conjugations.put("Imperativ", getGermanVerbImperativeConjugations("lerne", "lernt", "lernen"));
    return conjugations;
  }

  private Map<String, String> getGermanVerbTenseConjugations(String ich, String du, String erSieEs, String wir, String ihr, String sieSie) {
    Map<String, String> conjugations = new HashMap<>();
    conjugations.put("ich", ich);
    conjugations.put("du", du);
    conjugations.put("er/sie/es", erSieEs);
    conjugations.put("wir", wir);
    conjugations.put("ihr", ihr);
    conjugations.put("sie/Sie", sieSie);
    return conjugations;
  }

  private Map<String, String> getGermanVerbImperativeConjugations(String du, String ihr, String Sie) {
    Map<String, String> conjugations = new HashMap<>();
    conjugations.put("du", du);
    conjugations.put("ihr", ihr);
    conjugations.put("Sie", Sie);
    return conjugations;
  }
}
