package com.qualitypaper.fluentfusion.controller.dto.request;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class AddTranslationRequest {
  private Long id;
  private String translation;
  private PartOfSpeech partOfSpeech;
  private Long vocabularyGroupId;
}
