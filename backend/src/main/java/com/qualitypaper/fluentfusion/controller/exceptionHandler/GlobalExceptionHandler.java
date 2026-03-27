package com.qualitypaper.fluentfusion.controller.exceptionHandler;

import com.qualitypaper.fluentfusion.exception.VocabularyOwnerException;
import com.qualitypaper.fluentfusion.exception.notfound.NotFoundException;
import com.qualitypaper.fluentfusion.service.email.FormResendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import software.amazon.awssdk.services.translate.model.ResourceNotFoundException;

import javax.naming.AuthenticationException;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

  private final FormResendService formResendService;

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
    log.error(ex.getMessage());
    return new ResponseEntity<>(new ErrorMessage(ex.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<?> handleNotFoundException(NotFoundException ex, WebRequest request) {
    formResendService.sendErrorMessage(ex.getMessage());
    log.error(ex.getMessage());
    return new ResponseEntity<>(new ErrorMessage(ex.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(VocabularyOwnerException.class)
  public ResponseEntity<?> handleVocabularyOwnerException(VocabularyOwnerException ex, WebRequest request) {
    formResendService.sendErrorMessage(ex.getMessage());
    log.error(ex.getMessage());
    return new ResponseEntity<>(new ErrorMessage(ex.getMessage()), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
    formResendService.sendErrorMessage(ex.getMessage());
    log.error(request.getRemoteUser(), ex.getMessage());
    return new ResponseEntity<>(new ErrorMessage(ex.getMessage()), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
    formResendService.sendErrorMessage(ex.getMessage());
    log.error(ex.getMessage());
    return new ResponseEntity<>(new ErrorMessage(ex.getMessage()), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
    formResendService.sendErrorMessage(ex.getMessage());
    log.error(ex.getMessage());
    ex.printStackTrace();
    return new ResponseEntity<>(new ErrorMessage(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
    formResendService.sendErrorMessage(ex.getMessage());
    log.error(ex.getMessage());
    return new ResponseEntity<>(new ErrorMessage(ex.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
    formResendService.sendErrorMessage(ex.getMessage());
    log.error(ex.getMessage());
    return new ResponseEntity<>(new ErrorMessage(ex.getMessage()), HttpStatus.BAD_REQUEST);
  }

  public record ErrorMessage(String error) {
  }
}
