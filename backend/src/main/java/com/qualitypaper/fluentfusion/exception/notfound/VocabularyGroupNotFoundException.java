package com.qualitypaper.fluentfusion.exception.notfound;

public class VocabularyGroupNotFoundException extends NotFoundException {

  public VocabularyGroupNotFoundException() {
    super("Vocabulary group not found");
  }

  public VocabularyGroupNotFoundException(long id) {
    super("Vocabulary group not found with id " + id);
  }
}
