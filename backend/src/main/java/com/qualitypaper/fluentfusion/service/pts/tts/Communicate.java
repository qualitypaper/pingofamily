package com.qualitypaper.fluentfusion.service.pts.tts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.HttpRequestStruct;
import com.qualitypaper.fluentfusion.util.HttpUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.http.Header;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class Communicate {

  private static final Logger log = LoggerFactory.getLogger(Communicate.class);
  private static final Random random = new Random();
  private static final String[] voiceGenders = new String[]{"MALE", "FEMALE"};

  private final HttpUtils httpUtils;
  private final GoogleTTSProperties ttsApiProperties;
  private final ObjectMapper mapper;
  private TTSConfig ttsConfig;

  //  public Communicate(
//          HttpUtils httpUtils, GoogleTTSProperties ttsApiProperties, ObjectMapper mapper,
//          Language language,
//          String voice,
//          float rate,
//          float volume,
//          float pitch
//  ) {
//    this.httpUtils = httpUtils;
//    this.ttsApiProperties = ttsApiProperties;
//    this.mapper = mapper;
//    this.ttsConfig = new TTSConfig(language, voice, rate, volume, pitch);
//  }
//
  @PostConstruct
  public void init() {
    initializeConfig();
  }

  private void initializeConfig() {
    this.ttsConfig = new TTSConfig(Language.ENGLISH);
  }

  public void reinitializeConfig(Language language, String voice, float rate, float volume, float pitch) {
    this.ttsConfig = new TTSConfig(language, voice, rate, volume, pitch);
  }

  public byte[] generateAudio(String text) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(Header.CONTENT_TYPE, "application/json");

    ObjectNode body = JsonNodeFactory.instance.objectNode();

    ObjectNode audioConfig = getAudioConfig();
    ObjectNode voiceConfig = getVoiceConfig();
    ObjectNode inputConfig = getInputConfig(text);

    body.set("audioConfig", audioConfig);
    body.set("voice", voiceConfig);
    body.set("input", inputConfig);

    HttpRequestStruct struct = HttpRequestStruct.builder()
            .headers(headers)
            .body(body.toString())
            .url(this.ttsApiProperties.synthesizeUrl() + "?key=" + this.ttsApiProperties.apiKey())
            .build();

    JsonNode response = httpUtils.post(struct);

    String audio = response.get("audioContent").asText();

    return Base64.getDecoder().decode(audio);
  }

  private ObjectNode getInputConfig(String text) {
    return JsonNodeFactory.instance.objectNode()
            .put("text", text);
  }

  private ObjectNode getVoiceConfig() {
    return JsonNodeFactory.instance.objectNode()
            .put("languageCode", this.ttsConfig.getLanguage().getCollapsed())
            .put("ssmlGender", Communicate.voiceGenders[random.nextInt(0, 1)])
            .put("modelName", "WaveNet");
  }

  private ObjectNode getAudioConfig() {
    return JsonNodeFactory.instance.objectNode()
            .put("audioEncoding", "mp3")
            .put("pitch", this.ttsConfig.getPitch())
            .put("speakingRate", this.ttsConfig.getRate())
            .put("volumeGainDb", this.ttsConfig.getVolume());
  }

  public void save(byte[] audio, String audioFilename) throws Exception {
    File audioFile = new File(audioFilename);
    if (audioFile.exists() && audioFile.length() != 0) {
      log.info("A not empty file with name: {}, already exists", audioFilename);
      return;
    }

    try (FileOutputStream audioStream = new FileOutputStream(audioFilename)) {
      audioStream.write(audio);
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }

}
