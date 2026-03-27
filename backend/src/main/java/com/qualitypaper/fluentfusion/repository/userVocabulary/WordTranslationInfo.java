package com.qualitypaper.fluentfusion.repository.userVocabulary;

/**
 * Projection for {@link com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation}
 */
public interface WordTranslationInfo {
  Long getId();

  WordInfo getWordFrom();

  WordInfo getWordTo();
}