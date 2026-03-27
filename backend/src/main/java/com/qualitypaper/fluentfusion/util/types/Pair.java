package com.qualitypaper.fluentfusion.util.types;

public record Pair<A, B>(A first, B second) {

  @Override
  public String toString() {
    return String.format("""
            Pair {
                first: %s,
                second: %s
            }
            """, this.first, this.second);
  }

}
