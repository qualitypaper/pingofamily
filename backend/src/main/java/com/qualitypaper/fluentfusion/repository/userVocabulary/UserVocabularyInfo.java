package com.qualitypaper.fluentfusion.repository.userVocabulary;

/**
 * Projection for {@link com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary}
 */
public interface UserVocabularyInfo {
  Long getId();

  WordTranslationInfo getWordTranslation();

  VocabularyGroupInfo getVocabularyGroup();
}