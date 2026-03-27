package com.qualitypaper.fluentfusion.exception.notfound;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
@Slf4j
public class NotFoundException extends RuntimeException {

  public NotFoundException(String message) {
    super(message);
    log.error(message);
  }
}
