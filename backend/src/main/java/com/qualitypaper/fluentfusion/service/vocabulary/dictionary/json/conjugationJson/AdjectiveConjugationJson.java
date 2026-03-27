package com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json.conjugationJson;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
public class AdjectiveConjugationJson extends ConjugationJson {

  @JsonProperty
  private Map<String, String> mappings;
}
