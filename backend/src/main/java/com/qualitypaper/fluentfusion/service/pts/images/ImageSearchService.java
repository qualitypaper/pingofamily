package com.qualitypaper.fluentfusion.service.pts.images;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.service.pts.translation.TranslationService;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.TranslationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageSearchService {

  private static final OkHttpClient okHttpClient = new OkHttpClient();

  private static final String SHUTTER_STOCK_URL = "https://api.shutterstock.com/v2/images/search?query=";
  private static final String SHUTTER_STOCK_USERNAME = "WyoRDS5sj4h8cdFsdaAG6t7OGAsupej3";
  private static final String SHUTTER_STOCK_PASSWORD = "gf4nrf0uAQqp1fhJ";
  private final TranslationService translationService;

  public String findImage(String query, Language language, boolean landscape) {
    if (!language.equals(Language.ENGLISH)) {
      query = translationService.translateText(query, language, Language.ENGLISH, TranslationProvider.AMAZON);
    }

    log.info("Searching for image: {}", query);
    String auth = "%s:%s".formatted(SHUTTER_STOCK_USERNAME, SHUTTER_STOCK_PASSWORD);
    Request request = new Request.Builder()
            .get()
            .url(SHUTTER_STOCK_URL + query)
            .addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes()))
            .build();

    try (Response response = okHttpClient.newCall(request).execute()) {
      if (!response.isSuccessful() || response.body() == null)
        throw new IOException("Unexpected code " + response);

      String responseBody = response.body().string();

      JsonObject res = new Gson().fromJson(responseBody, JsonObject.class);
      JsonArray urls = null;

      if (!res.get("data").isJsonNull() && res.get("data").isJsonArray()) {
        urls = res.get("data").getAsJsonArray();
      }

      if (urls == null) {
        if (landscape) return findImage(query, language, false);

        return null;
      }

      if (urls.isEmpty()) return "";

      JsonObject urlMap = urls.get(0).getAsJsonObject().get("assets").getAsJsonObject().get("preview").getAsJsonObject();

      String str = urlMap.get("url").getAsString();
      log.info("Found Image: {}", str);
      return str;
    } catch (IOException | NullPointerException e) {
      log.error("Error while searching for image: {}", e.getMessage(), e);
      return search(query, language);
    }
  }

  @SuppressWarnings("unchecked")
  public String search(String query, Language language) {
    String url = "https://api.pexels.com/v1/search?query=" + query + "&per_page=1";
    String key = "NGBCmGtqaaE0b9H7ZqHTf8ZBS0oEEPny44neIpMBPdkgj4YTAoIoOqOP";

    Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", key)
            .build();
    try (Response response = okHttpClient.newCall(request).execute()) {
      assert response.body() != null;
      Object photos = ((Map<String, Object>) new Gson().fromJson(response.body().string(), Map.class)).get("photos");
      if (photos == null) return "";

      Object photo;
      if (!((java.util.List<?>) photos).isEmpty()) {
        photo = ((List<?>) photos).getFirst();
      } else return "";

      return String.valueOf(((Map<String, Object>) ((Map<String, Object>) photo).get("src")).get("medium"));
    } catch (IOException e) {
      return findImage(query, language, false);
    }
  }
}