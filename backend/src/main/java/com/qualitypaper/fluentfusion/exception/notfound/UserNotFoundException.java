package com.qualitypaper.fluentfusion.exception.notfound;

public class UserNotFoundException extends NotFoundException {
  public UserNotFoundException() {
    super("User not found");
  }

  public UserNotFoundException(long id) {
    super("User not found with id " + id);
  }
}
