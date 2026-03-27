package com.qualitypaper.fluentfusion.mappers.word;

import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.WordListResponse;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.WordResponse;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;

public interface WordMapper {
  WordResponse mapFrom(Word word, boolean hideWordDictionary, Language targetLanguage);
  WordListResponse mapFrom(Object[] word);
}
