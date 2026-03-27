package com.qualitypaper.fluentfusion.mappers.wordExampleTranslation;

import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.WordExampleTranslationResponse;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordExampleTranslation;

public interface WordExampleTranslationMapper {
  WordExampleTranslationResponse mapFrom(WordExampleTranslation wordExampleTranslation);
}
