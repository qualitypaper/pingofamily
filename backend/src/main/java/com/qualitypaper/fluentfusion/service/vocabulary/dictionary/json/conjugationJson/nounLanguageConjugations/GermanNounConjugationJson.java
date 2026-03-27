package com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json.conjugationJson.nounLanguageConjugations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json.conjugationJson.NounConjugationJson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GermanNounConjugationJson extends NounConjugationJson {
  // fields infinitive and plural are captured with NounConjugationJson class

  // an example of the json
  //            "infinitive": "Buch",
  //            "gender": "neuter",
  //            "plural": "Bücher",
  //            "mappings": {
  //                "nominative": {
  //                    "singular": "Buch",
  //                    "plural": "Bücher"
  //                },
  //                "accusative": {
  //                    "singular": "Buch",
  //                    "plural": "Bücher"
  //                },
  //                "dative": {
  //                    "singular": "Buch",
  //                    "plural": "Büchern"
  //                },
  //                "genitive": {
  //                    "singular": "Buches",
  //                    "plural": "Bücher"
  //                }
  //            }
  @JsonProperty
  private String gender;
  @JsonProperty
  private Map<String, Map<String, String>> mappings;
}
