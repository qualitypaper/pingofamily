package com.qualitypaper.fluentfusion.model.vocabulary.word;

public enum WordType {
  // NO_NEED_TO_CHECK is used for articles and pronouns, as well as other static words in the languages,
  // such filtration is implemented in UnneededWordsService class
  // word -> 1 word, phrase -> 2 - 4, sentence -> 5 ...
  WORD, PHRASE, SENTENCE, NO_NEED_TO_CHECK,
  PHRASAL_VERB,
  GERMAN_REFLEXIVE_VERB /* sich interessieren  */,
  VERB_WITH_PREPOSITION /* beschäftigen mit  */,
  SPANISH_REFLEXIVE_VERB
}
