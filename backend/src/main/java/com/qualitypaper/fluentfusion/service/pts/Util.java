package com.qualitypaper.fluentfusion.service.pts;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class Util {

  @SuppressWarnings("unchecked")
  public static <T> Map<String, T> findJsonObject(String json) {
    String result = json.replace("\n", "").replace("null", "None");
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return result.startsWith("{") ?
              objectMapper.readValue(result, Map.class) :
              objectMapper.readValue(findJson(result), Map.class);
    } catch (Exception e) {
      log.error("An error occurred while trying to evaluate the response: {}", e.getMessage(), e);
      String filteredString = removeError(result);
      try {
        return objectMapper.readValue(filteredString, Map.class);
      } catch (Exception ex) {
        log.error("An error occurred while trying to evaluate the response: {}", ex.getMessage(), e);
        throw new RuntimeException("Couldn't find JSON object in the parsed string");
      }
    }
  }

  private static String removeError(String result) {
    // All the substitutions here are made from observations
    return result.replace("...", ":");
  }

  private static String findJson(String string) {
    int startCount = 0;
    int endCount = string.length() - 1;

    // Deletes from the beginning
    while (startCount < string.length() && string.charAt(startCount) != '{') {
      startCount++;
    }

    // Deletion starts from end
    while (endCount >= 0 && string.charAt(endCount) != '}') {
      endCount--;
    }

    if (startCount == 0 && endCount == string.length() - 1) {
      return "{}";
    }

    return string.substring(startCount, endCount + 1);
  }
}