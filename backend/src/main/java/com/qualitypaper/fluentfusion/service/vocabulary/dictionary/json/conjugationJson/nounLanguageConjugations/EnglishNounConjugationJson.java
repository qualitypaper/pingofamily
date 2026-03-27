package com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json.conjugationJson.nounLanguageConjugations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json.conjugationJson.NounConjugationJson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EnglishNounConjugationJson extends NounConjugationJson {

  // an example of json

  //            "infinitive": "book",
  //            "plural": "books",
  //            "possessive": "book's",
  //            "plural_possessive": "books'",
  @JsonProperty
  private String possessive;
  @JsonProperty("plural_possessive")
  private String pluralPossessive;
}
