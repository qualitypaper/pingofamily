package com.qualitypaper.fluentfusion.service.pts.dictionary.examples;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;

import java.util.HashMap;
import java.util.Map;

record SpanishConjugationExample(PartOfSpeech pos) implements ConjugationExampleFactory {

  private static Map<String, String> getSpanishVerbTenseConjugations(String yo, String tu, String elEllaUsted, String nosotros, String vosotros, String ellosEllasUstedes) {
    Map<String, String> conjugations = new HashMap<>();
    conjugations.put("yo", yo);
    conjugations.put("tú", tu);
    conjugations.put("él/ella/usted", elEllaUsted);
    conjugations.put("nosotros/nosotras", nosotros);
    conjugations.put("vosotros/vosotras", vosotros);
    conjugations.put("ellos/ellas/ustedes", ellosEllasUstedes);
    return conjugations;
  }

  // Special case for "se" verb in Spanish
  static Map<String, Object> getSpanishSeVerbExample() {
    Map<String, Object> seVerbExample = new HashMap<>();
    seVerbExample.put("infinitive", "se");
    seVerbExample.put("conjugations", getSpanishSeVerbConjugations());
    return seVerbExample;
  }

  private static Map<String, Object> getSpanishSeVerbConjugations() {
    Map<String, Object> conjugations = new HashMap<>();
    conjugations.put("Presente", getSpanishVerbTenseConjugations("me", "te", "se", "nos", "os", "se"));
    return conjugations;
  }

  private Map<String, Object> getSpanishNounExample() {
    Map<String, Object> nounExample = new HashMap<>();
    nounExample.put("singular_masculine", "libro");
    nounExample.put("singular_feminine", "casa");
    nounExample.put("plural_masculine", "libros");
    nounExample.put("plural_feminine", "casas");
    return nounExample;
  }

  private Map<String, Object> getSpanishAdjectiveExample() {
    Map<String, Object> adjectiveExample = new HashMap<>();
    adjectiveExample.put("word", "grande");
    adjectiveExample.put("mappings", getSpanishAdjectiveMappings());
    return adjectiveExample;
  }

  private Map<String, Object> getSpanishVerbExample() {
    Map<String, Object> verbExample = new HashMap<>();
    verbExample.put("infinitive", "aprender");
    verbExample.put("conjugations", getSpanishVerbConjugations());
    return verbExample;
  }

  @Override
  public Map<String, Object> getExample() {

    return switch (pos) {
      case NOUN -> getSpanishNounExample();
      case VERB -> getSpanishVerbExample();
      case ADJECTIVE -> getSpanishAdjectiveExample();
      default -> new HashMap<>();
    };
  }

  // Helper methods for Spanish examples
  private Map<String, String> getSpanishAdjectiveMappings() {
    Map<String, String> mappings = new HashMap<>();
    mappings.put("comparative", "más grande");
    mappings.put("superlative", "el/la más grande");
    return mappings;
  }

  private Map<String, Object> getSpanishVerbConjugations() {
    Map<String, Object> conjugations = new HashMap<>();
    conjugations.put("Presente", getSpanishVerbTenseConjugations("aprendo", "aprendes", "aprende", "aprendemos", "aprendéis", "aprenden"));
    conjugations.put("Futuro", getSpanishVerbTenseConjugations("aprenderé", "aprenderás", "aprenderá", "aprenderemos", "aprenderéis", "aprenderán"));
    conjugations.put("Imperfecto", getSpanishVerbTenseConjugations("aprendía", "aprendías", "aprendía", "aprendíamos", "aprendíais", "aprendían"));
    conjugations.put("Pretérito", getSpanishVerbTenseConjugations("aprendí", "aprendiste", "aprendió", "aprendimos", "aprendisteis", "aprendieron"));
    conjugations.put("Condicional", getSpanishVerbTenseConjugations("aprendería", "aprenderías", "aprendería", "aprenderíamos", "aprenderíais", "aprenderían"));
    conjugations.put("Subjuntivo", getSpanishVerbTenseConjugations("aprenda", "aprendas", "aprenda", "aprendamos", "aprendáis", "aprendan"));
    return conjugations;
  }

}
