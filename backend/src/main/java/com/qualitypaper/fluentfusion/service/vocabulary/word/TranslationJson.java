package com.qualitypaper.fluentfusion.service.vocabulary.word;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TranslationJson {

  @JsonProperty
  @Nullable
  private String translation;
  @JsonProperty
  @Nullable
  private String pos;
  @JsonProperty
  @Nullable
  private String gender;
}