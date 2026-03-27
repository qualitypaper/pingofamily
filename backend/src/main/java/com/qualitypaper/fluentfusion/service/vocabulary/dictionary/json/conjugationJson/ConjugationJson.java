package com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json.conjugationJson;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class ConjugationJson {

  @JsonProperty
  private String infinitive;
}
