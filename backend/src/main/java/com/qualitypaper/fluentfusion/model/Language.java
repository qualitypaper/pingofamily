package com.qualitypaper.fluentfusion.model;

public enum Language {
  GERMAN, ENGLISH, SPANISH, ROMANIAN, RUSSIAN;


  public static Language fromCollapsed(String targetLang) {
    return switch (targetLang) {
      case "en" -> ENGLISH;
      case "de" -> GERMAN;
      case "es" -> SPANISH;
      case "ro" -> ROMANIAN;
      case "ru" -> RUSSIAN;
      default -> null;
    };
  }

  public String getCollapsed() {
    return switch (this) {
      case ROMANIAN -> "ro";
      case ENGLISH -> "en";
      case GERMAN -> "de";
      case SPANISH -> "es";
      case RUSSIAN -> "ru";
    };
  }


}
