package com.qualitypaper.fluentfusion.mappers.wordTranslation;

import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.WordTranslationResponse;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;

public interface WordTranslationMapper {

  WordTranslationResponse mapFrom(WordTranslation wordTranslation);

  WordTranslationResponse mapFromWordTranslationWithoutWordDictionaries(WordTranslation wordTranslation);
}
