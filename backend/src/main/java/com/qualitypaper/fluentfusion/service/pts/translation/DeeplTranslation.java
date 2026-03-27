package com.qualitypaper.fluentfusion.service.pts.translation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qualitypaper.fluentfusion.model.Language;
import okhttp3.*;

public class DeeplTranslation {

  private final static String DEEPL_URL = "https://api-free.deepl.com/v2/translate";
  // "f1069f72-5211-4f25-8fe3-a4a26f0f2225:fx"
  private final static String DEEPL_KEY = "d58a3fb9-02d4-4780-893e-ca29768d3178:fx";
  private final static OkHttpClient okHttpClient = new OkHttpClient();

  public static String translate(String text, Language sourceLanguage, Language targetLanguage) throws RuntimeException {
    String targetFormattedLanguage = targetLanguage.equals(Language.ENGLISH) ? "en-US" : targetLanguage.getCollapsed();

    String body = String.format("""
                {
                    "text": ["%s"],
                    "source_lang": "%s",
                    "target_lang": "%s"
                }
            """, text, sourceLanguage.getCollapsed(), targetFormattedLanguage);

    RequestBody requestBody = RequestBody.create(body, MediaType.parse("application/json"));

    Request request = new Request.Builder()
            .url(DEEPL_URL)
            .header("Authorization", "DeepL-Auth-Key " + DEEPL_KEY)
            .post(requestBody)
            .build();

    try (Response response = okHttpClient.newCall(request).execute()) {
      if (response.body() == null) {
        throw new RuntimeException("Response body of Deepl wordTranslation request is null");
      } else if (response.code() / 100 == 4) {
        JsonObject jsonObject = new Gson().fromJson(response.body().string(), JsonObject.class);
        String message = jsonObject.get("message").getAsString();

        if (message.contains("Quota Exceeded")) {
          throw new RuntimeException("All keys are exhausted");
        }
      }

      JsonObject jsonObject = new Gson().fromJson(response.body().string(), JsonObject.class);
      System.out.println(jsonObject);

      JsonArray jsonArray = jsonObject.get("translations").getAsJsonArray();
      if (jsonArray.isEmpty()) return null;

      return jsonArray.get(0).getAsJsonObject().get("text").getAsString();
    } catch (Exception e) {
      System.out.println("Couldn't translate text: " + text);
      throw new RuntimeException(e);
    }
  }
}