package com.qualitypaper.fluentfusion.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qualitypaper.fluentfusion.service.vocabulary.word.TranslationJson;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddWordRequest {
  @JsonProperty("tempId")
  private String tempWordId;
  private String word;
  @JsonProperty("wordTranslation")
  private TranslationJson translationJson;
  private Long vocabularyId;
  private Long vocabularyGroupId;
}
