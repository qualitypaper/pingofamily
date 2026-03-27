package com.qualitypaper.fluentfusion.service.db.types;

import org.springframework.lang.Nullable;

public record Compare(String column, Comparison comparison, Object value, @Nullable FilterSeparator nextSeparator) {
  public Compare(String column, Object value) {
    this(column, Comparison.EQUALS, value, null);
  }

  public static Compare eq(String column, Object value, FilterSeparator nextSeparator) {
    return new Compare(column, Comparison.EQUALS, value, nextSeparator);
  }

  public static Compare is(String column, Object value, FilterSeparator nextSeparator) {
    return new Compare(column, Comparison.IS, value, nextSeparator);
  }

  public static Compare is(String column, Object value) {
    return new Compare(column, Comparison.IS, value, null);
  }

  public static Compare isNot(String column, Object value, FilterSeparator nextSeparator) {
    return new Compare(column, Comparison.IS_NOT, value, nextSeparator);
  }

  public static Compare isNot(String column, Object value) {
    return new Compare(column, Comparison.IS_NOT, value, null);
  }

  public static Compare eq(String column, Object value) {
    return new Compare(column, Comparison.EQUALS, value, null);
  }

  public static Compare ne(String column, Object value, FilterSeparator nextSeparator) {
    return new Compare(column, Comparison.NOT_EQUALS, value, nextSeparator);
  }

  public static Compare ne(String column, Object value) {
    return new Compare(column, Comparison.NOT_EQUALS, value, null);
  }

  public static Compare gt(String column, Object value, FilterSeparator nextSeparator) {
    return new Compare(column, Comparison.GREATER_THAN, value, nextSeparator);
  }

  public static Compare gt(String column, Object value) {
    return new Compare(column, Comparison.GREATER_THAN, value, null);
  }

  public static Compare lt(String column, Object value, FilterSeparator nextSeparator) {
    return new Compare(column, Comparison.LESS_THAN, value, nextSeparator);
  }

  public static Compare lt(String column, Object value) {
    return new Compare(column, Comparison.LESS_THAN, value, null);
  }

  public static Compare gte(String column, Object value, FilterSeparator nextSeparator) {
    return new Compare(column, Comparison.GREATER_THAN_OR_EQUALS, value, nextSeparator);
  }

  public static Compare gte(String column, Object value) {
    return new Compare(column, Comparison.GREATER_THAN_OR_EQUALS, value, null);
  }

  public static Compare lte(String column, Object value, FilterSeparator nextSeparator) {
    return new Compare(column, Comparison.LESS_THAN_OR_EQUALS, value, nextSeparator);
  }

  public static Compare lte(String column, Object value) {
    return new Compare(column, Comparison.LESS_THAN_OR_EQUALS, value, null);
  }

  public static Compare like(String column, Object value, FilterSeparator nextSeparator) {
    return new Compare(column, Comparison.LIKE, value, nextSeparator);
  }

  public static Compare like(String column, Object value) {
    return new Compare(column, Comparison.LIKE, value, null);
  }

  public static Compare ilike(String column, Object value, FilterSeparator nextSeparator) {
    return new Compare(column, Comparison.ILIKE, value, nextSeparator);
  }

  public static Compare ilike(String column, Object value) {
    return new Compare(column, Comparison.ILIKE, value, null);
  }

  public static Compare in(String column, Object value, FilterSeparator nextSeparator) {
    return new Compare(column, Comparison.IN, value, nextSeparator);
  }

  public static Compare in(String column, Object value) {
    return new Compare(column, Comparison.IN, value, null);
  }
}
