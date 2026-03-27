package com.qualitypaper.fluentfusion.service.pts.tts;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.qualitypaper.fluentfusion.util.Utils;
import lombok.NoArgsConstructor;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor
class VoiceManager {
  private List<Map<String, Object>> voices = new ArrayList<>();
  private boolean calledCreate = false;

  public static CompletableFuture<VoiceManager> create(List<Map<String, Object>> customVoices) {
    return CompletableFuture.supplyAsync(() -> {
      VoiceManager manager = new VoiceManager();
      try {
        if (customVoices == null) {
          manager.voices = listVoices().get();
        } else {
          manager.voices = customVoices;
        }
      } catch (Exception e) {
        Utils.printStackTrace(e.getStackTrace());
      }

      // Add language attribute to each voice based on the Locale
      for (Map<String, Object> voice : manager.voices) {
        String locale = (String) voice.get("Locale");
        voice.put("Language", locale.split("-")[0]);
      }

      manager.calledCreate = true;
      return manager;
    });
  }

  // Method to list all available voices
  public static CompletableFuture<List<Map<String, Object>>> listVoices() {
    return CompletableFuture.supplyAsync(() -> {
      try (HttpClient client = HttpClient.newBuilder()
              .sslContext(createSSLContext())
              .build()) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Constant.VOICE_LIST))
                .header("Authority", "speech.platform.bing.com")
                .header("Sec-CH-UA", "\" Not;A Brand\";v=\"99\", \"Microsoft Edge\";v=\"91\", \"Chromium\";v=\"91\"")
                .header("Sec-CH-UA-Mobile", "?0")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.864.41")
                .header("Accept", "*/*")
                .header("Sec-Fetch-Site", "none")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Accept-Language", "en-US,en;q=0.9")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String responseBody = response.body();
        JsonArray data = new Gson().fromJson(responseBody, JsonArray.class);
        List<Map<String, Object>> voicesList = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
          JsonObject voice = data.get(i).getAsJsonObject();
          if (voice == null) continue;
          Map<String, Object> tempMap = new HashMap<>();
          voice.entrySet().forEach(entry -> {
            if (entry != null) {
              tempMap.put(entry.getKey(), entry.getValue());
            }
          });
          voicesList.add(tempMap);
        }
        return voicesList;
      } catch (IOException | InterruptedException | CertificateException e) {
        e.printStackTrace();
        return Collections.emptyList();
      }
    });
  }

  // Helper method to create SSLContext with a trusted certificate
  private static SSLContext createSSLContext() throws CertificateException {
    try {
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init((java.security.KeyStore) null);

      X509TrustManager x509TrustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, new javax.net.ssl.TrustManager[]{x509TrustManager}, null);

      return sslContext;
    } catch (Exception e) {
      throw new CertificateException("Error creating SSLContext", e);
    }
  }

  // Method to find all matching voices based on provided attributes
  public List<Map<String, Object>> find(Map<String, Object> attributes) {
    if (!calledCreate) {
      throw new IllegalStateException("VoicesManager.find() called before VoicesManager.create()");
    }

    List<Map<String, Object>> matchingVoices = new ArrayList<>();
    for (Map<String, Object> voice : voices) {
      if (voice.entrySet().containsAll(attributes.entrySet())) {
        matchingVoices.add(voice);
      }
    }
    return matchingVoices;
  }

//    public static void main(String[] args) {
//        try {
//            // Example usage:
//            VoiceManager manager = VoiceManager.create(null).get();
//            Map<String, Object> searchAttributes = new HashMap<>();
//            searchAttributes.put("Language", "en");
//            List<Map<String, Object>> results = manager.find(searchAttributes);
//            for (Map<String, Object> voice : results) {
//                System.out.println(voice);
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//        }
//    }
}
