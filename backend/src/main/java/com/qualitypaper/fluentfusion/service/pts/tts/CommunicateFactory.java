package com.qualitypaper.fluentfusion.service.pts.tts;

import com.qualitypaper.fluentfusion.model.Language;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
public class CommunicateFactory {

  @Lookup
  public Communicate createCommunicate() {
    // This method will be implemented by Spring's @Lookup proxy
    return null;
  }

  public Communicate createCommunicate(Language language, String voice, float rate, float volume, float pitch) {
    Communicate communicate = createCommunicate();
    communicate.reinitializeConfig(language, voice, rate, volume, pitch);

    return communicate;
  }

  public Communicate createCommunicate(Language language, String voice) {
    Communicate communicate = createCommunicate();
    communicate.reinitializeConfig(language, voice, 0, 0, 0);

    return communicate;
  }
}