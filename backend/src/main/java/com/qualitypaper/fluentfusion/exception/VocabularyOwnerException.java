package com.qualitypaper.fluentfusion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class VocabularyOwnerException extends RuntimeException {

  public VocabularyOwnerException() {
    super("Wrong vocabulary owner");
  }

  public VocabularyOwnerException(String message) {
    super(message);
  }
}