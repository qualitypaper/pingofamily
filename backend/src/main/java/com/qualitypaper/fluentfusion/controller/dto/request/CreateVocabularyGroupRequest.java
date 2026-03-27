package com.qualitypaper.fluentfusion.controller.dto.request;

import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.VocabularyResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateVocabularyGroupRequest {
  private Long vocabularyId;
  private String name;
  private VocabularyResponse vocabularyResponse;
}
