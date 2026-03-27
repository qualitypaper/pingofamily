package com.qualitypaper.fluentfusion.service.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.service.vocabulary.word.TranslationJson;
import io.lettuce.core.RedisCommandTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

  private final RedisTemplate<String, String> template;
  private final ObjectMapper objectMapper;

  public void save(String key, String data) {
    template.opsForValue().set(key, data);
  }

  public List<String> autocomplete(String key, Language sourceLanguage, Language targetLanguage) {
    try {
      Set<String> keys = template.opsForValue().getOperations().keys(formatKey(key + "*", sourceLanguage, targetLanguage));
      if (keys == null) return new ArrayList<>();

      return keys.stream().map(this::extractWordFromKey).sorted(Comparator.comparingInt(String::length)).toList();
    } catch (RedisCommandTimeoutException e) {
      return new ArrayList<>();
    }
  }

  public Set<String> getKeysLike(String regex) {
    return template.opsForValue().getOperations().keys(regex);
  }

  public String get(String key) {
    return template.opsForValue().get(key);
  }

  public List<Map<String, String>> getTranslations(String key) {
    if (key == null || key.isEmpty())
      return new ArrayList<>();
    try {
      String value = template.opsForValue().get(key);
      if (value == null) {
        return new ArrayList<>();
      } else if (value.startsWith("\"")) {
        value = value.substring(1, value.length() - 1);
      }
      return objectMapper.readValue(value, new TypeReference<>() {});
    } catch (Exception e) {
      log.error("Error while parsing translations for key: {}", key);
      return new ArrayList<>();
    }
  }


  public List<TranslationJson> getTranslationsJson(String word, Language sourceLanguage, Language targetLanguage) {
    return getTranslationsJson(formatKey(word, sourceLanguage, targetLanguage));
  }

  public String formatKey(String word, Language sourceLanguage, Language targetLanguage) {
    return "%s_%s_(%s)".formatted(sourceLanguage.getCollapsed(), targetLanguage.getCollapsed(), word.trim());
  }

  public String formatKey(String word, String sourceLanguage, String targetLanguage) {
    return "%s_%s_(%s)".formatted(sourceLanguage, targetLanguage, word.trim());
  }

  public List<TranslationJson> getTranslationsJson(String key) {
    return map(getTranslations(key));
  }

  public List<TranslationJson> map(List<Map<String, String>> list) {
    return list == null ? new ArrayList<>()
            : list.stream().map(e -> new TranslationJson(e.get("translation"), e.get("pos"), "")).toList();
  }

  public void put(String word, Map<String, String> translation, Language source, Language target) {
    String key = formatKey(word, source, target);

    put(key, translation);
  }

  public void put(String key, Map<String, String> translation) {
    List<Map<String, String>> currentTranslations = getTranslations(key);
    if (currentTranslations == null || currentTranslations.isEmpty()) {
      save(key, new Gson().toJson(translation));
    } else {
      boolean contains = false;
      for (Map<String, String> currentTranslation : currentTranslations) {
        if (currentTranslation.get("pos").equals(translation.get("pos"))
                && currentTranslation.get("sentenceTranslation").equals(translation.get("sentenceTranslation"))) {
          contains = true;
          break;
        }
      }
      if (!contains) {
        currentTranslations.add(translation);
        save(key, new Gson().toJson(currentTranslations));
      }

    }
  }

  @Async
  public void trimAllKeys() {
    Set<String> keys = template.keys("*");

    keys.parallelStream().forEach(key -> {

      if (key.contains("(")) {
        String[] split = key.split("_");
        if (split.length == 3) {

          String word = split[2].substring(1, split[2].length() - 1);
          String source = split[0];
          String target = split[1];

          String newKey = formatKey(word.trim(), source, target);

          if (!key.equals(newKey)) {
            List<Map<String, String>> translations = getTranslations(key);
            template.delete(key);
            save(newKey, new Gson().toJson(translations));
          }
        }
      }
    });
    log.info("Trimmed all keys in Redis");
  }

  public String extractWordFromKey(String key) {

    if (key.contains("(")) {
      String[] split = key.split("_");
      if (split.length == 3) {
        return split[2].substring(1, split[2].length() - 1);
      }
    }

    return null;
  }

  public Set<String> getKeysWithTranslation(String translation, Set<String> keys) {
    Set<String> result = new HashSet<>();

    for (String key : keys) {
      List<Map<String, String>> translations = getTranslations(key);
      if (translations == null || translations.isEmpty()) continue;

      for (Map<String, String> map : translations) {
        if (map.get("sentenceTranslation").equals(translation)) {
          result.add(key);
          break;
        }
      }
    }

    return result;
  }
}
