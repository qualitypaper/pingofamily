package com.qualitypaper.fluentfusion.controller.dto.response.vocabulary;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WordListResponse {
  private String tempWordId;
  private Long userVocabularyId;
  private Long wordTranslationId;
  private Long wordFromId;
  private Long wordToId;
  private String wordFrom;
  private String wordTo;
  private String soundUrl;
  private Long vocabularyGroupId;
  private String partOfSpeech;

  private Long previousUserVocabularyId;
  private String error;
  private ResponseStatus status;
}
