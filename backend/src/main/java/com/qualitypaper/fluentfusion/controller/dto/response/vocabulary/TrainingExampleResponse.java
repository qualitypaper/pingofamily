package com.qualitypaper.fluentfusion.controller.dto.response.vocabulary;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class TrainingExampleResponse {
  private Long trainingId;
  private WordTranslationResponse wordTranslation;
  private List<TrainingExampleListResponse> trainingExampleList;
}
