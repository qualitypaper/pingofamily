package com.qualitypaper.fluentfusion.service.email;

import org.springframework.stereotype.Component;

@Component
public interface UserChecker {

  static boolean isValidEmail(String email) {
    return email.matches("\\b([A-z0-9!#$%&'*-/=?^_`{|}~]+)+@([A-z0-9.-]+)+\\.([A-z|]{2,})\\b");
  }

  static boolean isValidPassword(String password) {
    return password.length() >= 4;
  }
}
