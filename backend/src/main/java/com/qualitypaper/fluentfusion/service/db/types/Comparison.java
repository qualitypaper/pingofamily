package com.qualitypaper.fluentfusion.service.db.types;


import lombok.Getter;

@Getter
public enum Comparison {
  EQUALS("="),
  NOT_EQUALS("<>"),
  GREATER_THAN(">"),
  LESS_THAN("<"),
  GREATER_THAN_OR_EQUALS(">="),
  LESS_THAN_OR_EQUALS("<="),
  LIKE("LIKE"),
  ILIKE("ILIKE"),
  IN("IN"),
  IS("IS"),
  IS_NOT("IS NOT");

  public final String value;

  Comparison(String value) {
    this.value = value;
  }
}
