package com.qualitypaper.fluentfusion.service.db.types;

public enum FilterSeparator {
  AND("AND"),
  OR("OR"),
  AND_NOT("AND NOT"),
  NOT("NOT");

  public final String value;

  FilterSeparator(String value) {
    this.value = value;
  }
}
