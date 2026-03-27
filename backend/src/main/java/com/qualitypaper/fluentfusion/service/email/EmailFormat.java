package com.qualitypaper.fluentfusion.service.email;

public interface EmailFormat {

  static String process(String email) {
    if (email == null || email.isEmpty()) {
      return "";
    }

    return email
            .trim()
            .replaceAll("\\s+", "")
            .toLowerCase();
  }
}
