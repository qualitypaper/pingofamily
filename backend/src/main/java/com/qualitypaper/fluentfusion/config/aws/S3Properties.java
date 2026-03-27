package com.qualitypaper.fluentfusion.config.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "service.aws.s3")
public record S3Properties(String accessKeyId, String secretKey, String bucket) {
}
