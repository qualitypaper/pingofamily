package com.qualitypaper.fluentfusion.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CompleteTrainingSessionTypesRequest {
  private Long trainingExampleId;
  private List<CompleteTrainingExample> answers;
}
