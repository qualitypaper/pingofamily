package com.qualitypaper.fluentfusion.config.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "service.aws.translate")
public record TranslateProperties(String accessKeyId, String secretKey) {

}
