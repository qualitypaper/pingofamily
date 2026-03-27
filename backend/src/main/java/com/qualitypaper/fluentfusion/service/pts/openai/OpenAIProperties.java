package com.qualitypaper.fluentfusion.service.pts.openai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "service.openai")
public record OpenAIProperties(String url, String apiKey, String gptModel) {
}
