package com.qualitypaper.fluentfusion.service.pts.dictionary;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.service.pts.openai.OpenAiCompletionService;
import com.qualitypaper.fluentfusion.util.JsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class DescriptionService {

  private final OpenAiCompletionService openAiCompletionService;
  private final ObjectMapper objectMapper;

  @Async
  CompletableFuture<String> getDescription(String text, Language sourceLanguage, PartOfSpeech partOfSpeech) {
    Map<String, String> example = Collections.singletonMap("desc", "Word description wordTranslation");
    String prompt = String.format("""
            Generate a simple description of the '%s' in %s language and in %s part of speech,
            without mentioning the part of speech or the word itself give the response in json, example: %s,
            give the response description in %s language""", text, sourceLanguage, partOfSpeech, example, sourceLanguage);

    for (int i = 0; i < 3; i++) {
      String completion = openAiCompletionService.createCompletion(prompt, new ArrayList<>());

      try {
        Map<String, String> jsonResponse = objectMapper.readValue(JsonService.findJson(completion), new TypeReference<>() {
        });

        String desc = jsonResponse.get("desc");

        if (desc == null || desc.isEmpty()) {
          continue;
        }

        return CompletableFuture.completedFuture(desc);
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    }

    throw new IllegalStateException("Failed to get description for word: " + text + " in language: " + sourceLanguage);
  }
}
