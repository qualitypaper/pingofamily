package com.qualitypaper.fluentfusion.service.db.queries.resultTypes;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GetWords {
  public Long userVocabularyId;
  public Long wordTranslationId;
  public Long wordFromId;
  public Long wordToId;
  public String wordFromWord;
  public String wordToWord;
  public String soundUrl;
  public Long vocabularyGroupId;
  public String partOfSpeech;
  public Long createdAt;
}
