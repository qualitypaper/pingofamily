package com.qualitypaper.fluentfusion.service.vocabulary.structs;

import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.Difficulty;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ExampleTranslationStruct {

  private String word;
  private String translation;
  private PartOfSpeech partOfSpeech;
  private Language sourceLanguage;
  private Language targetLanguage;
  private Difficulty difficulty;

  public ExampleTranslationStruct reverse() {
    return new ExampleTranslationStruct(translation, word,
            partOfSpeech, targetLanguage, sourceLanguage, difficulty);
  }
}