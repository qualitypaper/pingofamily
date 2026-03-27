package com.qualitypaper.fluentfusion.mappers.wordExampleTranslation;

import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.WordExampleTranslationResponse;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordExampleTranslation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class WordExampleTranslationMapperImplementation implements WordExampleTranslationMapper {

  @Value("${url.static.audio}")
  private String staticAudioUrl;

  @Override
  public WordExampleTranslationResponse mapFrom(WordExampleTranslation wordExampleTranslation) {
    if (wordExampleTranslation == null ||
            wordExampleTranslation.getWordTranslation() == null ||
            wordExampleTranslation.getWordExampleFrom() == null ||
            wordExampleTranslation.getWordExampleTo() == null) return null;

    byte[] exampleFrom = wordExampleTranslation.getWordExampleFrom().getExample().getBytes(StandardCharsets.UTF_8);
    byte[] exampleTo = wordExampleTranslation.getWordExampleTo().getExample().getBytes(StandardCharsets.UTF_8);

    return new WordExampleTranslationResponse(
            wordExampleTranslation.getId(),
            new String(exampleFrom, StandardCharsets.UTF_8),
            new String(exampleTo, StandardCharsets.UTF_8),
            staticAudioUrl + wordExampleTranslation.getWordExampleFrom().getSoundUrl(),
            wordExampleTranslation.getWordTranslation().getId()
    );
  }

}

