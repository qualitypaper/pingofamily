package com.qualitypaper.fluentfusion.service.pts.tts;

import com.qualitypaper.fluentfusion.model.Language;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
class TTSConfig {

  private final Language language;
  private final float rate;
  private final float volume;
  private final float pitch;
  // Getters for accessing the values
  @Setter
  private String voice;

  public TTSConfig(Language language) {
    this.language = language;
    this.rate = 0;
    this.volume = 0;
    this.pitch = 0;
    this.voice = "";
  }

  public TTSConfig(Language language, String voice, float rate, float volume, float pitch) {
    this.language = language;
    this.voice = voice;
    this.rate = rate;
    this.volume = volume;
    this.pitch = pitch;

    validateAndSetVoice(voice);
  }

  private void validateAndSetVoice(String voice) {
    Pattern pattern = Pattern.compile("^([a-z]{2,})-([A-Z]{2,})-(.+Neural)$");
    Matcher match = pattern.matcher(voice);

    if (match.find()) {
      String lang = match.group(1);
      String region = match.group(2);
      String name = match.group(3);

      if (name.contains("-")) {
        region = region + "-" + name.substring(0, name.indexOf("-"));
        name = name.substring(name.indexOf("-") + 1);
      }

      this.voice = "(" + lang + "-" + region + ", " + name + ")";
    }
  }

}

