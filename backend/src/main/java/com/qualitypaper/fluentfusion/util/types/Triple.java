package com.qualitypaper.fluentfusion.util.types;

import java.util.Objects;

public record Triple<A, B, C>(A first, B second, C third) {

  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    else if (obj == this) return true;

    if (!obj.getClass().equals(this.getClass())) return false;

    try {
      Triple<A, B, C> casted = (Triple<A, B, C>) obj;

      return Objects.equals(this.first, casted.first()) &&
              Objects.equals(this.second, casted.second()) &&
              Objects.equals(this.third, casted.third());
    } catch (ClassCastException e) {
      return false;
    }
  }

  @Override
  public String toString() {
    return "Triple{first: %s, second: %s, third: %s}";
  }
}
