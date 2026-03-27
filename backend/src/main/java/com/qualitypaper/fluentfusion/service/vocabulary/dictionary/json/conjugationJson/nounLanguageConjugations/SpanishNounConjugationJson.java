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
public class SpanishNounConjugationJson extends NounConjugationJson {
  // values that are parsed from NounConjugationJson are singular and plural values for masculin

  // an example of mapping
  //             "singular_masculine": "libro",
  //            "singular_feminine": "casa",
  //            "plural_masculine": "libros",
  //            "plural_feminine": "casas"
  @JsonProperty("singular_feminine")
  private String singularFeminine;
  @JsonProperty("plural_feminine")
  private String pluralFeminine;
}
