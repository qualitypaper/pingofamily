package com.qualitypaper.fluentfusion.service.pts.dictionary;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.service.pts.dictionary.conjugation.EnglishVerb;
import com.qualitypaper.fluentfusion.service.pts.dictionary.conjugation.GermanVerb;
import com.qualitypaper.fluentfusion.service.pts.dictionary.conjugation.SpanishVerb;
import com.qualitypaper.fluentfusion.util.HttpUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UltraLinguaService {

  private final HttpUtils httpUtils;
  private final ObjectMapper objectMapper;

  @Value("${service.ultra-lingua.api-key}")
  private String ultraLinguaKey;
  @Value("${service.ultra-lingua.url}")
  private String ultraLinguaUrl;

  public Map<String, Map<String, String>> conjugate(String word, Language language) {
  ArrayNode list = callService(UltraLinguaServices.CONJUGATIONS, language.getCollapsed(), word);
    if (list.isEmpty()) {
      return Map.of();
    }

    ArrayNode conjugations = (ArrayNode) list.get(0).get("conjugations");

    return switch (language) {
      case GERMAN -> GermanVerb.parseGermanConjugation(conjugations);
      case SPANISH -> SpanishVerb.parseSpanishConjugations(conjugations);
      case ENGLISH -> EnglishVerb.parseEnglishConjugations(conjugations);
      case ROMANIAN, RUSSIAN -> Map.of();
    };
  }

  public Optional<List<DefinitionsResponse>> lookup(String word, Language sourceLanguage, Language targetLanguage) {
    ArrayNode array = callService(UltraLinguaServices.DEFINITIONS, sourceLanguage.getCollapsed(), targetLanguage.getCollapsed(), word);

    try {
      return Optional.ofNullable(objectMapper.convertValue(array, new TypeReference<>() {
      }));

    } catch (IllegalArgumentException _) {
      return Optional.empty();
    }
  }

  public List<String> findLemma(String word, Language language) {
    try {
      ArrayNode list = callService(UltraLinguaServices.LEMMAS, language.getCollapsed(), word);
      List<String> lemmas = new ArrayList<>();
      for (JsonNode jsonElement : list) {
        lemmas.add(jsonElement.get("root").asText());
      }

      return lemmas;
    } catch (Exception e) {
      return List.of(word);
    }
  }

  /**
   * @param service - one of provided services
   * @param params  - the order matters, refer to ultra lingua docs for more insight
   * @return api response
   */
  public ArrayNode callService(UltraLinguaServices service, Object... params) {
    if (Arrays.stream(params).anyMatch(e -> e == null || (e instanceof String casted && casted.isEmpty()))) {
      return new ArrayNode(JsonNodeFactory.instance);
    }
    String url = buildUrl(service, params);
    JsonNode node = httpUtils.get(url, new HttpHeaders());
    if (node == null || node.equals(NullNode.instance)) {
      return new ArrayNode(JsonNodeFactory.instance);
    }
    return (ArrayNode) node;
  }

  private String buildUrl(UltraLinguaServices service, Object... params) {

    StringBuilder builder = new StringBuilder(ultraLinguaUrl);
    if (ultraLinguaUrl.charAt(ultraLinguaUrl.length() - 1) != '/') {
      builder.append('/');
    }
    builder.append(service.getService());

    for (Object param : params) {
      builder.append('/');
      builder.append(param);
    }

    builder.append("?key=").append(ultraLinguaKey);

    return builder.toString();
  }


  @Getter
  public enum UltraLinguaServices {
    DEFINITIONS("definitions"),
    CONJUGATIONS("conjugations"),
    LEMMAS("lemmas"),
    SYNONYMS("synonyms"),
    ANTONYMS("antonyms"),
    SINGULARS("singulars"),
    PLURALS("plurals"),
    TERMS("terms");

    private final String service;

    UltraLinguaServices(String service) {
      this.service = service;
    }
  }

  public record DefinitionsResponse(String surfaceform, String text, String root, PartOfSpeechResponse partofspeech) {
  }

  public record PartOfSpeechResponse(String gender, String partofspeechcategory, String number) {
  }
}
