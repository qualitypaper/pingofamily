package com.qualitypaper.fluentfusion.controller.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompleteTrainingTrainingIdResults {
  private Long trainingId;
  private List<CompleteTrainingSessionTypesRequest> mistakes;
}
