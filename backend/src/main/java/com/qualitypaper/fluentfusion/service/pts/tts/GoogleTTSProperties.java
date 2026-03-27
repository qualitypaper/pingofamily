package com.qualitypaper.fluentfusion.service.pts.tts;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="service.google.tts")
public record GoogleTTSProperties(String synthesizeUrl, String voicesUrl, String keysJsonPath, String apiKey) {
}
