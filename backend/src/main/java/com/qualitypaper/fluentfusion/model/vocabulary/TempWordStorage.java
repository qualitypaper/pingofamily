package com.qualitypaper.fluentfusion.model.vocabulary;

import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TempWordStorage {

  private String word;
  private String translation;
  private PartOfSpeech partOfSpeech;
  private Language learningLanguage;
  private Language nativeLanguage;
  private VocabularyGroup vocabularyGroup;
}
