package com.qualitypaper.fluentfusion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class CopyException extends RuntimeException {
  public CopyException(String message) {
    super(message);
    System.out.println(message);
  }
}
