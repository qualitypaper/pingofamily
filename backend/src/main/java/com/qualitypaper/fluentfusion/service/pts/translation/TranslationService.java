package com.qualitypaper.fluentfusion.service.pts.translation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.qualitypaper.fluentfusion.config.ApplicationConfig;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.LemmaResponse;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.PossibleTranslationsResponse;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.service.db.RedisService;
import com.qualitypaper.fluentfusion.service.pts.dictionary.UltraLinguaService;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.WordDictionaryService;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.TranslationProvider;
import com.qualitypaper.fluentfusion.service.vocabulary.word.TranslationJson;
import com.qualitypaper.fluentfusion.service.vocabulary.word.UnneededWordsService;
import com.qualitypaper.fluentfusion.service.vocabulary.word.WordService;
import com.qualitypaper.fluentfusion.util.Cache;
import com.qualitypaper.fluentfusion.util.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class TranslationService {

  private final HttpUtils httpService;
  private final RedisService redisService;
  private final TranslateClient translateClient;
  private final Cache<String, PossibleTranslationsResponse> translationsCache = new Cache<>(100, 3_600_000);
  private final UnneededWordsService unneededWordsService;
  private final UltraLinguaService ultraLinguaService;

  @Value("${microservice.pts.host}")
  private String ptsUrl;

  private static String removeArticles(String text, Language sourceLanguage) {
    return (switch (sourceLanguage) {
      case ENGLISH -> text.replaceAll("\\b(a|an|the)\\b", "");
      case GERMAN -> text.replaceAll("\\b(ein|eine|der|die|das)\\b", "");
      case SPANISH -> text.replaceAll("\\b(un|una|el|la|los|las)\\b", "");
      default -> text;
    }).trim();
  }

  public PossibleTranslationsResponse getPossibleTranslations(String text, Language sourceLanguage, Language targetLanguage) {
    text = removeArticles(text, sourceLanguage);
    var cached = translationsCache.get(text + sourceLanguage.getCollapsed());
    if (cached.isPresent()) {
      return cached.get();
    }
    boolean isComplexWord = text.split(" ").length != 1;
    WordDetails wordDetails = getWordDetails(text, sourceLanguage);

    String word;
    if (isComplexWord) {
      word = text;
    } else {
      word = wordDetails.lemma();
    }

    List<TranslationJson> translations = redisService.getTranslationsJson(word, sourceLanguage, targetLanguage);
    if (translations == null || translations.isEmpty() || isComplexWord) {
      String translation;
      try {
        translation = DeeplTranslation.translate(word, sourceLanguage, targetLanguage);
      } catch (Exception _) {
        log.error("Couldn't get translations for text: {}", word);
        translation = translateAmazon(word, sourceLanguage, targetLanguage);
      }

      String[] res = unneededWordsService.removeUnneededPart(translation, targetLanguage);
      String pos = null;
      if (unneededWordsService.removeVerbIdentifiers(res[1]).isEmpty()) {
        pos = PartOfSpeech.VERB.name();
      }

      WordDetails details = getWordDetails(res[0], targetLanguage);

      TranslationJson translationJson = new TranslationJson(res[0], pos != null ? pos : details.pos(), details.gen());
      if (translations == null) {
        translations = List.of(translationJson);
      } else {
        translations = new ArrayList<>(translations);
        translations.add(translationJson);
      }
    }

    var response = new PossibleTranslationsResponse(
            new LemmaResponse(wordDetails.lemma(), WordDictionaryService.mapPos(wordDetails.pos())),
            translations
    );
    translationsCache.put(text + sourceLanguage.getCollapsed(), response);
    return response;
  }

  public String getPos(String word, Language lang) {
    if (lang != Language.ENGLISH && lang != Language.SPANISH && lang != Language.GERMAN) {
      log.error("Language not supported for POS tagging: {}", lang);
      return PartOfSpeech.OTHER.name();
    } else if (word == null || word.isBlank()) {
      log.error("Word is null or empty");
      return PartOfSpeech.OTHER.name();
    }
    ObjectNode res = (ObjectNode) httpService.get(ptsUrl + "/pos?word=" + URLEncoder.encode(word, StandardCharsets.UTF_8) + "&lang=" + lang.getCollapsed(), new HttpHeaders());

    return res.get("data").get(0).get("pos").asText();
  }

  /**
   * slower than the typical getPos method,
   * uses external services (UltraLingua API) for a more correct prediction
   */
  public PartOfSpeech getDetailedPos(String word, String translation, Language source, Language target) {
    Optional<List<UltraLinguaService.DefinitionsResponse>> lookup = ultraLinguaService.lookup(word, source, target);

    if (lookup.isEmpty()) throw new IllegalStateException("Lookup service returned nothing");

    for (UltraLinguaService.DefinitionsResponse definitionsResponse : lookup.get()) {
      if (definitionsResponse.text().equals(translation)) {
        return PartOfSpeech.valueOf(definitionsResponse.partofspeech().partofspeechcategory().toUpperCase());
      }
    }

    return PartOfSpeech.OTHER;
  }

  public WordDetails getWordDetails(String word, Language language) {
    if (word == null || word.isBlank())
      return initializeDefault();

    JsonNode res = httpService.get(
            "%s/%s?word=%s".formatted(ptsUrl, language.getCollapsed(), URLEncoder.encode(word, StandardCharsets.UTF_8)),
            new HttpHeaders());

    if (res == null)
      return initializeDefault();

    ArrayNode data = (ArrayNode) res.get("data");
    if (data == null)
      return initializeDefault();

    ObjectNode details = (ObjectNode) data.get(ApplicationConfig.RANDOM.nextInt(data.size()));


    details.put("Pos", WordService.mapPos(details.get("Pos").asText()));

    JsonNode pos = details.get("Pos");
    JsonNode gender = details.get("Gender");
    JsonNode lemma = details.get("Lemma");
    JsonNode mood = details.get("Mood");
    JsonNode number = details.get("Number");
    JsonNode person = details.get("Person");
    JsonNode tense = details.get("Tense");
    JsonNode verbForm = details.get("VerbForm");
    JsonNode cas = details.get("Case");

    return new WordDetails(
            pos != null ? pos.asText() : "",
            gender != null ? gender.asText() : "",
            lemma != null ? lemma.asText() : "",
            mood != null ? mood.asText() : "",
            number != null ? number.asText() : "",
            person != null ? person.asText() : "",
            tense != null ? tense.asText() : "",
            verbForm != null ? verbForm.asText() : "",
            cas != null ? cas.asText() : ""
    );

  }

  private WordDetails initializeDefault() {
    return new WordDetails(PartOfSpeech.OTHER.name(), "", "", "", "", "", "", "", "");
  }

  public String translateText(String text, Language sourceLanguage, Language targetLanguage,
                              TranslationProvider translationProvider) {
    return switch (translationProvider) {
      case DEEPL -> {
        try {
          yield DeeplTranslation.translate(text, sourceLanguage, targetLanguage);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          yield translateText(text, sourceLanguage, targetLanguage, TranslationProvider.AMAZON);
        }
      }
      case AMAZON -> translateAmazon(text, sourceLanguage, targetLanguage);
    };
  }

  private String translateAmazon(String text, Language sourceLang, Language targetLang) {
    try {

      TranslateTextResponse translateTextResponse = translateClient.translateText(TranslateTextRequest.builder()
              .text(text)
              .sourceLanguageCode(sourceLang.getCollapsed())
              .targetLanguageCode(targetLang.getCollapsed())
              .build());

      return translateTextResponse.translatedText();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      log.error("Couldn't translate text: {}", text);
      throw new IllegalStateException(e);
    }
  }

  public List<String> autocomplete(String str, Language sourceLanguage, Language targetLanguage) {
    return redisService.autocomplete(str, sourceLanguage, targetLanguage).stream().limit(10).toList();
  }

  public record WordDetails(String pos, String gen, String lemma, String mood, String number, String person,
                            String tense,
                            String verbForm, String case_) {
  }
}
