package com.qualitypaper.fluentfusion.exception.notfound;

public class VocabularyNotFoundException extends NotFoundException {
  public VocabularyNotFoundException() {
    super("Vocabulary not found");
  }

  public VocabularyNotFoundException(long id) {
    super("Vocabulary not found with id " + id);
  }
}
