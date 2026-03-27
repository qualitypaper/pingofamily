package com.qualitypaper.fluentfusion.service.pts.dictionary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.service.pts.openai.OpenAiCompletionService;
import com.qualitypaper.fluentfusion.util.JsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class ThesaurusService {

  private final OpenAiCompletionService openAiCompletionService;
  private final ObjectMapper objectMapper;

  @Async
  public CompletableFuture<List<String>> getSynonyms(String text, Language sourceLanguage) {

    String prompt = String.format("""
                Get as much the most straightforward synonyms for the word '%s' in language: %s,
                the response must be presented in list[str] format, without any other text at all,
                only list, without anything apart from that
            """, text, sourceLanguage);

    for (int i = 0; i < 3; i++) {
      String completion = openAiCompletionService.createCompletion(prompt, new ArrayList<>());
      completion = completion.replaceAll("'", "\"");
      String json = JsonService.findJson(completion);

      try {
        try {
          return CompletableFuture.completedFuture(objectMapper.readValue(json, List.class));
        } catch (IOException e) {
          return CompletableFuture.completedFuture(objectMapper.readValue(findPythonList(completion), List.class));
        }
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
    }

    return CompletableFuture.completedFuture(Collections.emptyList());
  }

  private String findPythonList(String str) {
    if (str.contains("list[str]")) {
      return "[" + str.substring(str.indexOf(']') + 1) + "]";
    }

    return JsonService.findJson(str);
  }

}
