package com.qualitypaper.fluentfusion.service.pts.tts;

import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.service.pts.s3.ObjectType;
import com.qualitypaper.fluentfusion.service.pts.s3.S3Service;
import com.qualitypaper.fluentfusion.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class TextToSpeechService {

  private final S3Service s3Service;

  @Value("${spring.web.resources.static-locations}")
  private String tempDirPath;

  private final CommunicateFactory communicateFactory;

  private String writeTextToSpeech(String text, Language language) {
    String filename = StringUtils.encodeMD5(text + language.toString()) + ".mp3";
    String filepath = tempDirPath + filename;

    for (int i = 7; i >= 0; i--) {
      Communicate communicate = communicateFactory.createCommunicate(language, "");
      try {
        byte[] audio = communicate.generateAudio(text);
        communicate.save(audio, filepath);
      } catch (Exception e) {
        continue;
      }

      File file = new File(filepath);

      if (!file.exists() || file.length() == 0) {
        continue;
      }

      return filepath;
    }

    log.error("File wasn't saved with tts: {}\nContent: {}", filename, text);
    throw new RuntimeException("File wasn't saved with tts: {}\nContent: " + text);
  }

  public String generateSoundFile(String text, Language language) {
    String filepath = writeTextToSpeech(text, language);

    String key = s3Service.uploadObject(filepath, ObjectType.AUDIO);
    try {
      Files.delete(Path.of(filepath));
    } catch (IOException e) {
      log.error("Failed to delete file: {}", filepath, e);
    }
    return key;
  }

  public String generateLocalSoundFile(String text, Language language) {
    return writeTextToSpeech(text, language);
  }
}
