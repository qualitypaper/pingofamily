package com.qualitypaper.fluentfusion.repository.userVocabulary;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;

import java.time.LocalDateTime;

/**
 * Projection for {@link com.qualitypaper.fluentfusion.model.vocabulary.word.Word}
 */
public interface WordInfo {
  Long getId();

  String getWord();

  String getSoundUrl();

  PartOfSpeech getPos();

  LocalDateTime getCreatedAt();
}