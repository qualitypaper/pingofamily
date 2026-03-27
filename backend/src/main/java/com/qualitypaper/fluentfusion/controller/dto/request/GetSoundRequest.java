package com.qualitypaper.fluentfusion.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class GetSoundRequest {
  boolean speechStatus;
  private String text;
  private String language;
}
