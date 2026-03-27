package com.qualitypaper.fluentfusion.service.pts.examples;

import com.qualitypaper.fluentfusion.model.Language;
import org.jetbrains.annotations.NotNull;

public record ExamplesResponse(Language language, String example) {
  @NotNull
  @Override
  public String toString() {
    return language + ": " + example;
  }
}
