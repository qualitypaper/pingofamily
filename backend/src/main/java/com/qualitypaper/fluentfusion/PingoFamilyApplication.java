package com.qualitypaper.fluentfusion;

import com.qualitypaper.fluentfusion.config.RsaKeyProperties;
import com.qualitypaper.fluentfusion.config.aws.S3Properties;
import com.qualitypaper.fluentfusion.config.aws.TranslateProperties;
import com.qualitypaper.fluentfusion.service.pts.openai.OpenAIProperties;
import com.qualitypaper.fluentfusion.service.pts.tts.GoogleTTSProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.qualitypaper.fluentfusion.repository")
@EnableCaching
@EnableConfigurationProperties({
        OpenAIProperties.class, S3Properties.class,
        TranslateProperties.class, RsaKeyProperties.class,
        GoogleTTSProperties.class
})
public class PingoFamilyApplication {

  public static void main(String[] args) {
    SpringApplication.run(PingoFamilyApplication.class, args);
  }

}
