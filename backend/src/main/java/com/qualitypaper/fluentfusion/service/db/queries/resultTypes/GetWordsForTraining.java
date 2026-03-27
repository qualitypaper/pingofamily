package com.qualitypaper.fluentfusion.service.db.queries.resultTypes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetWordsForTraining {
  public Long pr;
  public Long userVocabularyId;
  public Boolean isNew;
  public Long lastTrainingMillis;
  public Integer priority;
  public Long trainingExampleId;
  public Long wordTranslationId;
  public Long wordFromId;
  public Long wordToId;
  public String wordFrom;
  public String wordFromSoundUrl;
  public String imageUrl;
  public String wordTo;
  public String wordToSoundUrl;
  public String pos;
  public Long trainingId;
  public String formattedString;
  public String identifiedWord;
  public String trainingType;
  public String soundUrl;
  public String trainingExampleSentence;
  public String trainingExampleSentenceTranslation;

}

