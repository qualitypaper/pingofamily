package com.qualitypaper.fluentfusion.service.db.types;

public enum SqlValue {
  NULL("NULL"),
  TRUE("TRUE"),
  FALSE("FALSE");

  private final String value;

  SqlValue(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
