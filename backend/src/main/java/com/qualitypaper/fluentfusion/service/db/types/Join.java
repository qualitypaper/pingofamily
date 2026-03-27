package com.qualitypaper.fluentfusion.service.db.types;

public record Join(String tableName, String column, String joinColumn, JoinType joinType, String alias) {

  public Join(String tableName, String column, String joinColumn, JoinType joinType) {
    this(tableName, column, joinColumn, joinType, null);
  }

  public Join(String tableName, String column, String joinColumn) {
    this(tableName, column, joinColumn, JoinType.INNER, null);
  }

  public Join(String tableName, String column, String joinColumn, String alias) {
    this(tableName, column, joinColumn, JoinType.INNER, alias);
  }

  public static Join inner(String tableName, String column, String joinColumn, String alias) {
    return new Join(tableName, column, joinColumn, JoinType.INNER, alias);
  }

  public static Join inner(String tableName, String column, String joinColumn) {
    return new Join(tableName, column, joinColumn, JoinType.INNER, null);
  }

  public static Join left(String tableName, String column, String joinColumn, String alias) {
    return new Join(tableName, column, joinColumn, JoinType.LEFT, alias);
  }

  public static Join left(String tableName, String column, String joinColumn) {
    return new Join(tableName, column, joinColumn, JoinType.LEFT, null);
  }

  public static Join right(String tableName, String column, String joinColumn, String alias) {
    return new Join(tableName, column, joinColumn, JoinType.RIGHT, alias);
  }

  public static Join right(String tableName, String column, String joinColumn) {
    return new Join(tableName, column, joinColumn, JoinType.RIGHT, null);
  }

  public static Join full(String tableName, String column, String joinColumn, String alias) {
    return new Join(tableName, column, joinColumn, JoinType.FULL, alias);
  }

  public static Join full(String tableName, String column, String joinColumn) {
    return new Join(tableName, column, joinColumn, JoinType.FULL, null);
  }

  public static Join cross(String tableName, String column, String joinColumn, String alias) {
    return new Join(tableName, column, joinColumn, JoinType.CROSS, alias);
  }

  public static Join cross(String tableName, String column, String joinColumn) {
    return new Join(tableName, column, joinColumn, JoinType.CROSS, null);
  }

  public static Join natural(String tableName, String column, String joinColumn, String alias) {
    return new Join(tableName, column, joinColumn, JoinType.NATURAL, alias);
  }

  public static Join natural(String tableName, String column, String joinColumn) {
    return new Join(tableName, column, joinColumn, JoinType.NATURAL, null);
  }

  public static Join naturalLeft(String tableName, String column, String joinColumn, String alias) {
    return new Join(tableName, column, joinColumn, JoinType.NATURAL_LEFT, alias);
  }

  public static Join naturalLeft(String tableName, String column, String joinColumn) {
    return new Join(tableName, column, joinColumn, JoinType.NATURAL_LEFT, null);
  }

  public static Join naturalRight(String tableName, String column, String joinColumn, String alias) {
    return new Join(tableName, column, joinColumn, JoinType.NATURAL_RIGHT, alias);
  }

  public static Join naturalRight(String tableName, String column, String joinColumn) {
    return new Join(tableName, column, joinColumn, JoinType.NATURAL_RIGHT, null);
  }

  public static Join naturalFull(String tableName, String column, String joinColumn, String alias) {
    return new Join(tableName, column, joinColumn, JoinType.NATURAL_FULL, alias);
  }

  public static Join naturalFull(String tableName, String column, String joinColumn) {
    return new Join(tableName, column, joinColumn, JoinType.NATURAL_FULL, null);
  }

  public static Join naturalCross(String tableName, String column, String joinColumn, String alias) {
    return new Join(tableName, column, joinColumn, JoinType.NATURAL_CROSS, alias);
  }

  public static Join naturalCross(String tableName, String column, String joinColumn) {
    return new Join(tableName, column, joinColumn, JoinType.NATURAL_CROSS, null);
  }

  public static Join naturalInner(String tableName, String column, String joinColumn, String alias) {
    return new Join(tableName, column, joinColumn, JoinType.NATURAL_INNER, alias);
  }

  public static Join naturalInner(String tableName, String column, String joinColumn) {
    return new Join(tableName, column, joinColumn, JoinType.NATURAL_INNER, null);
  }

  public static Join naturalOuter(String tableName, String column, String joinColumn, String alias) {
    return new Join(tableName, column, joinColumn, JoinType.NATURAL_OUTER, alias);
  }

  public static Join naturalOuter(String tableName, String column, String joinColumn) {
    return new Join(tableName, column, joinColumn, JoinType.NATURAL_OUTER, null);
  }

  @Override
  public String toString() {
    return "Join{" +
            "tableName='" + tableName + '\'' +
            ", column='" + column + '\'' +
            ", joinColumn='" + joinColumn + '\'' +
            ", joinType=" + joinType +
            ", alias='" + alias + '\'' +
            '}';
  }
}
