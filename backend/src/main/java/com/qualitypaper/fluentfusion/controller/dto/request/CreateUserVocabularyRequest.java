package com.qualitypaper.fluentfusion.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserVocabularyRequest {

  private String learningLanguage;
  private String nativeLanguage;
}
