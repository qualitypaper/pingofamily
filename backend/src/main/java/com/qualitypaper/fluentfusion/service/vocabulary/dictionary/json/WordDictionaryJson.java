package com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WordDictionaryJson {

  @JsonProperty
  private String desc;
  @JsonProperty
  private String gender;
  @JsonProperty
  private List<String> synonyms;
  @JsonProperty
  private Object conjugation;
  @JsonProperty
  private String pos;

}
