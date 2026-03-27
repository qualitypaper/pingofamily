package com.qualitypaper.fluentfusion.service.pts.dictionary;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordType;
import com.qualitypaper.fluentfusion.service.pts.dictionary.examples.ConjugationExample;
import com.qualitypaper.fluentfusion.service.pts.openai.OpenAiCompletionService;
import com.qualitypaper.fluentfusion.util.JsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
@RequiredArgsConstructor
public class AIConjugationService {

  private final OpenAiCompletionService openAiCompletionService;
  private final ObjectMapper objectMapper;
  private final UltraLinguaService ultraLinguaService;

  @Async
  CompletableFuture<Object> makeConjugation(String text, Language sourceLanguage, PartOfSpeech partOfSpeech, WordType wordType) {
    if (sourceLanguage.equals(Language.ROMANIAN)) {
      return CompletableFuture.completedFuture(Collections.emptyMap());
    } else if (!partOfSpeech.equals(PartOfSpeech.ADJECTIVE)
            && !partOfSpeech.equals(PartOfSpeech.VERB)
            && !partOfSpeech.equals(PartOfSpeech.NOUN)) {
      return CompletableFuture.completedFuture(Collections.emptyMap());
    } else if (// test for spanish conjugations
            PartOfSpeech.VERB.equals(partOfSpeech) &&
                    (WordType.WORD.equals(wordType)
                            || WordType.GERMAN_REFLEXIVE_VERB.equals(wordType)
                            || WordType.PHRASAL_VERB.equals(wordType))
                    && !Language.ENGLISH.equals(sourceLanguage)) {

      for (int i = 0; i < 3; i++) {
        try {
          return CompletableFuture.completedFuture(
                  ultraLinguaService.conjugate(text, sourceLanguage)
          );
        } catch (Exception e) {
          log.error(e.getMessage());
        }
      }
    }

    for (int i = 0; i < 3; i++) {
      try {
        String prompt = getString(text, sourceLanguage, partOfSpeech, wordType);
        String completion = openAiCompletionService.createCompletion(prompt, new ArrayList<>());

        Map<String, Object> dictionary = objectMapper.readValue(
                JsonService.findJson(completion),
                new TypeReference<>() {
                  @Override
                  public Type getType() {
                    return new TypeReference<Map<String, Object>>() {
                    }.getType();
                  }
                }
        );

        return CompletableFuture.completedFuture(
                filterUnneededKeys(text, sourceLanguage, dictionary, partOfSpeech, wordType)
        );
      } catch (Exception e) {
        log.error(e.getMessage());
        log.info("Retrying chatgpt completion");
      }
    }

    return CompletableFuture.completedFuture(Collections.emptyMap());
  }

  private @NotNull String getString(String text, Language sourceLanguage, PartOfSpeech pos, WordType wordType) {
    String example = new Gson().toJson(getExample(pos, sourceLanguage, wordType));

    return String.format("""
            Generate conjugations for the word '%s' in %s as well as the part of speech that you should inspect %s.
            The specific part of speech is mentioned, so don't use any others for the conjugation, but don't be mixed up with the gender,
            only one gender must be specified in the response. The response format must be json, here is the: %s the format is strict without any additions and fixes,
            the response without any additional information excluding conjugation, stick strict to the example with all the variants of the conjugation""", text, sourceLanguage, pos, example);
  }

  private Map<String, Object> filterUnneededKeys(String text, Language sourceLanguage, Map<String, Object> dictionary, PartOfSpeech pos, WordType wordType) {
    Map<String, Object> example = getExample(pos, sourceLanguage, wordType);
    Map<String, Object> finalMap = new HashMap<>();

    dictionary.forEach((key, value) -> {
      if (example.containsKey(key) || "pos".equals(key)) {
        finalMap.put(key, value);
      }
    });

    if (Language.SPANISH.equals(sourceLanguage) &&
            PartOfSpeech.NOUN.equals(pos)
            && !allStrings(finalMap.values())) {
      Map<String, Object> mappings = dictionary.containsKey("mappings")
              ? (Map<String, Object>) dictionary.get("mappings")
              : (Map<String, Object>) dictionary.get("masculine");

      if (mappings == null) {
        mappings = (Map<String, Object>) dictionary.get("feminine");
      }
      finalMap.put("mappings", filterNounSpanish(mappings));
      finalMap.put("word", text);
    }

    return finalMap;
  }

  private boolean allStrings(Collection<Object> values) {
    return values.stream().allMatch(String.class::isInstance);
  }

  private Map<String, Object> filterNounSpanish(Map<String, Object> mappings) {
    if (mappings == null || mappings.isEmpty()) return new HashMap<>();

    Map<String, Object> finalMap = new HashMap<>();
    mappings.forEach((key, value) -> {
      if (value instanceof Map) {
        finalMap.putAll(constructAdjectiveMappingsWithDicts(mappings));
      } else {
        finalMap.put(key, value);
      }
    });
    return finalMap;
  }

  private Map<String, Object> constructAdjectiveMappingsWithDicts(Map<String, Object> map) {
    Map<String, Object> finalMap = new HashMap<>();
    map.forEach((key, value) -> {
      if ("feminine".equals(key)) {
        Map<String, Object> feminine = (Map<String, Object>) value;
        finalMap.put("feminine_singular", feminine.get("singular"));
        finalMap.put("feminine_plural", feminine.get("plural"));
      } else if ("masculine".equals(key)) {
        Map<String, Object> masculine = (Map<String, Object>) value;
        finalMap.put("masculine_singular", masculine.get("singular"));
        finalMap.put("masculine_plural", masculine.get("plural"));
      }
    });
    return finalMap;
  }

  private Map<String, Object> getExample(PartOfSpeech pos, Language sourceLanguage, WordType wordType) {
    ConjugationExample conjugationExample = new ConjugationExample(sourceLanguage, pos, wordType);
    return conjugationExample.getExample();
  }
}
