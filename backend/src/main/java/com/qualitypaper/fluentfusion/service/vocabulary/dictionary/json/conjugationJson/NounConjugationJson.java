package com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json.conjugationJson;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NounConjugationJson extends ConjugationJson {

  @JsonProperty
  private String plural;
  @JsonProperty
  private String infinitive;
}
