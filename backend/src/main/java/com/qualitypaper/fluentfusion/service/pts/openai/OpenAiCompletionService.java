package com.qualitypaper.fluentfusion.service.pts.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiCompletionService {

  private final ObjectMapper objectMapper;
  private final OkHttpClient okHttpClient;
  private final OpenAIProperties properties;

  public String createCompletion(String prompt, List<Map<String, String>> messages) {
    messages.add(Map.of("role", "user", "content", prompt));
    Map<String, Object> json = new HashMap<>();
    json.put("model", properties.gptModel());
    json.put("messages", messages);

    RequestBody body;
    try {
      body = RequestBody.create(objectMapper.writeValueAsString(json), MediaType.parse("application/json"));
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
      throw new IllegalStateException(e);
    }

    Request request = new Request.Builder()
            .url(properties.url())
            .header("Authorization", "Bearer " + properties.apiKey())
            .header("Content-Type", "application/json")
            .post(body)
            .build();

    try (Response response = okHttpClient.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Unexpected code " + response);
      }

      var responseBody = response.body();
      if (responseBody == null)
        throw new NullPointerException("Null body in createCompletion(), run with prompt: \t" + prompt);

      JsonNode rootNode = objectMapper.readTree(responseBody.string());
      String content = rootNode.get("choices").get(0).get("message").get("content").asText();
      log.debug(content);

      return content;
    } catch (IOException _) {
      return createCompletion(prompt, messages);
    }
  }
}
