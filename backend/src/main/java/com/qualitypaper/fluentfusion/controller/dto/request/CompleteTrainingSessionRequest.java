package com.qualitypaper.fluentfusion.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompleteTrainingSessionRequest {

  private Long learningSessionId;
  private List<CompleteTrainingTrainingIdResults> results;
}
