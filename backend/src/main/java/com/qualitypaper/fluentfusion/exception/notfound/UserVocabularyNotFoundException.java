package com.qualitypaper.fluentfusion.exception.notfound;

public class UserVocabularyNotFoundException extends NotFoundException {

  public UserVocabularyNotFoundException() {
    super("UserVocabulary not found");
  }

  public UserVocabularyNotFoundException(long id) {
    super("UserVocabulary not found with id: " + id);
  }
}
