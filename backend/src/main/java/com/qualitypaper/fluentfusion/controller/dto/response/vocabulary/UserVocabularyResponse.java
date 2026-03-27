package com.qualitypaper.fluentfusion.controller.dto.response.vocabulary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.Training;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(value = {"createdAt"})
public class UserVocabularyResponse implements Serializable {
  private String tempWordId;
  private ResponseStatus status;
  private Long userVocabularyId;
  private Training nextTraining;
  private WordTranslationResponse wordTranslation;
  private WordExampleTranslationResponse wordExampleTranslation;
  private Long userID;
  private String error;
  private Long vocabularyGroupId;
}
