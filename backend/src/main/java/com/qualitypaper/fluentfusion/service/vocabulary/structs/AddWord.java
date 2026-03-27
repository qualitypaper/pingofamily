package com.qualitypaper.fluentfusion.service.vocabulary.structs;

import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslationType;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordType;
import com.qualitypaper.fluentfusion.service.vocabulary.word.TranslationJson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AddWord {
  private String word;
  private TranslationJson translation;
  private User user;
  private UserVocabulary userVocabulary;
  private Vocabulary vocabulary;
  private WordTranslationType wordTranslationType;
  private WordType wordType;

  // to skip user field, as long userVocabulary has it
  public AddWord(String word, TranslationJson translation, UserVocabulary userVocabulary, WordTranslationType wordTranslationType, WordType wordType) {
    this.word = word;
    this.translation = translation;
    this.userVocabulary = userVocabulary;
    this.wordTranslationType = wordTranslationType;
    this.wordType = wordType;
  }

  public AddWord(String word,WordType wordType) {
    this.word = word;
    this.wordType = wordType;
  }


  public static AddWord copy(AddWord addWord) {
    return AddWord.builder()
            .word(addWord.getWord())
            .translation(addWord.getTranslation())
            .user(addWord.getUser())
            .userVocabulary(addWord.getUserVocabulary())
            .vocabulary(addWord.getVocabulary())
            .wordTranslationType(addWord.getWordTranslationType())
            .wordType(addWord.getWordType())
            .build();
  }

  public static AddWord copyWithNewWord(AddWord addWord, String newWord) {
    AddWord copy = copy(addWord);
    copy.setWord(newWord);
    return copy;
  }

  public static AddWord copyWithNewWordAndWordType(AddWord addWord, String newWord, WordType wordType) {
    AddWord copy = copyWithNewWord(addWord, newWord);
    copy.setWordType(wordType);
    return copy;
  }
}