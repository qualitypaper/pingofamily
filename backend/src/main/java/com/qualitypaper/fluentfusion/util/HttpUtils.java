package com.qualitypaper.fluentfusion.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.qualitypaper.fluentfusion.service.email.FormResendService;
import com.qualitypaper.fluentfusion.service.typealiase.MapSB;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.HttpRequestStruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpUtils {

  private static final RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
  private static final RestTemplate restTemplate = restTemplateBuilder.build();

  static {
    restTemplateBuilder.readTimeout(Duration.of(5, ChronoUnit.SECONDS));
    restTemplateBuilder.connectTimeout(Duration.of(3, ChronoUnit.SECONDS));
  }

  private final FormResendService formResendService;
  private final ObjectMapper objectMapper;


  public JsonNode post(HttpRequestStruct httpRequestStruct) {
    JsonNode response;

    if (httpRequestStruct.getHeaders() == null) {
      httpRequestStruct.setHeaders(new HttpHeaders());
    }

    if (httpRequestStruct.getHeaders().get(HttpHeaders.CONTENT_TYPE) == null) {
      httpRequestStruct.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json");
    }

    HttpEntity<String> request = new HttpEntity<>(httpRequestStruct.getBody().replaceAll("\n", ""),
            httpRequestStruct.getHeaders());

    if (httpRequestStruct.isLogBody())
      System.out.printf("Sending POST request with body: \n %s%n", httpRequestStruct.getBody());

    try {
      response = restTemplate.postForObject(httpRequestStruct.getUrl(), request, JsonNode.class);
    } catch (HttpServerErrorException.InternalServerError | HttpServerErrorException.GatewayTimeout e) {
      log.error(e.getMessage(), httpRequestStruct.getUrl());
      formResendService.sendErrorMessage(e.getMessage(), httpRequestStruct.getUrl());
      return JsonNodeFactory.instance.nullNode();
    }
    return response;
  }

  public JsonNode get(String url, HttpHeaders httpHeaders) {
    return get(url, httpHeaders, JsonNode.class).orElse(JsonNodeFactory.instance.nullNode());
  }

  public <T> Optional<T> get(String url, HttpHeaders httpHeaders, Class<T> clazz) {

    if (httpHeaders == null) {
      httpHeaders = new HttpHeaders();
    }

    if (httpHeaders.get(HttpHeaders.CONTENT_TYPE) == null) {
      httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/json");
    }

    try {
      String response = restTemplate.getForObject(url, String.class);

      return Optional.ofNullable(objectMapper.readValue(response, clazz));
    } catch (Exception e) {
      log.error(e.getMessage(), url);
      formResendService.sendErrorMessage(e.getMessage(), url);
      return Optional.empty();
    }
  }

  public static MapSB successResponse() {
    MapSB mapSB = new MapSB();
    mapSB.put("success", true);
    return mapSB;
  }
}
