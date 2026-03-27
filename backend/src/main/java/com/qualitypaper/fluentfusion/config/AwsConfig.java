package com.qualitypaper.fluentfusion.config;

import com.qualitypaper.fluentfusion.config.aws.S3Properties;
import com.qualitypaper.fluentfusion.config.aws.TranslateProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.translate.TranslateClient;

@Configuration
@RequiredArgsConstructor
public class AwsConfig {

  private final S3Properties s3Properties;
  private final TranslateProperties translateProperties;

  @Bean
  public S3Client s3Client() {
    AwsCredentials s3ClientCredentials = AwsBasicCredentials.create(s3Properties.accessKeyId(), s3Properties.secretKey());

    return S3Client.builder()
            .credentialsProvider(StaticCredentialsProvider.create(s3ClientCredentials))
            .region(Region.EU_CENTRAL_1)
            .build();
  }

  @Bean
  public TranslateClient translateClient() {
    AwsCredentials translationClientCredentials = AwsBasicCredentials.create(translateProperties.accessKeyId(), translateProperties.secretKey());

    return TranslateClient.builder()
            .credentialsProvider(StaticCredentialsProvider.create(translationClientCredentials))
            .region(Region.EU_CENTRAL_1)
            .build();
  }
}
