package com.qualitypaper.fluentfusion.service.db.queries.resultTypes;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;


@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GetWord {
  public String[] synonyms;
  public Long id;
  public Long wordTranslationId;
  public Long wordExampleTranslationId;
  public Long wordFromId;
  public Long wordToId;
  public String wordFrom;
  public String wordTo;
  public String partOfSpeech;
  public Long wordExampleFromId;
  public Long wordExampleToId;
  public String wordExampleFrom;
  public String wordExampleTo;
  public String wordExampleFromSoundUrl;
  public String gender;
  public String soundUrl;
  public String imageUrl;
  public Long vocabularyGroupId;

  public Long wordDictionaryId;
  public String learningLanguage;
  public String nativeLanguage;
  public Long conjugationId;
  public String desc;
  public String descriptionTranslation;
  public String tense;
  public String tenseKey;
  public String tenseValue;
  public String adjectiveKey;
  public String adjectiveValue;
  public String mapKey;
  public String key;
  public String value;
  public String singularMasculine;
  public String pluralMasculine;
  public String singularFeminine;
  public String pluralFeminine;
  public String possessive;
  public String pluralPossessive;
}
