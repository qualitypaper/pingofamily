package com.qualitypaper.fluentfusion.mappers.wordTranslation;

import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.WordTranslationResponse;
import com.qualitypaper.fluentfusion.mappers.word.WordMapper;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WordTranslationMapperImplementation implements WordTranslationMapper {

  private final WordMapper wordMapper;

  @Override
  public WordTranslationResponse mapFrom(WordTranslation wordTranslation) {
    if (wordTranslation == null) return null;

    return new WordTranslationResponse(
            wordTranslation.getId(),
            wordMapper.mapFrom(wordTranslation.getWordTo(), true, wordTranslation.getWordTo().getLanguage()),
            wordMapper.mapFrom(wordTranslation.getWordFrom(), false, wordTranslation.getWordTo().getLanguage())
    );
  }

  @Override
  public WordTranslationResponse mapFromWordTranslationWithoutWordDictionaries(WordTranslation wordTranslation) {
    if (wordTranslation == null) return null;

    return new WordTranslationResponse(
            wordTranslation.getId(),
            wordMapper.mapFrom(wordTranslation.getWordTo(), true, wordTranslation.getWordTo().getLanguage()),
            wordMapper.mapFrom(wordTranslation.getWordFrom(), true, wordTranslation.getWordTo().getLanguage())
    );
  }
}
