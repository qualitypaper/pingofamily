package com.qualitypaper.fluentfusion.service.email;

import com.qualitypaper.fluentfusion.controller.dto.request.FormBodyRequest;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.HttpRequestStruct;
import com.qualitypaper.fluentfusion.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class FormResendService {

    private static final String SLACK_ERROR_URL = "--slack-error-url--";
    private static final String SLACK_INFO_URL = "--slack-info-url--";
    private static final String TELEGRAM_BOT_URL = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
    private static final String TELEGRAM_BOT_TOKEN = "--telegram-bot-token--";
    private static final String TELEGRAM_CHAT_ID = "--telegram-chat-id--";
    private static final RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
    private static final RestTemplate restTemplate = restTemplateBuilder.build();

    static {
        restTemplateBuilder.readTimeout(Duration.of(20, ChronoUnit.SECONDS));
    }

    public void sendErrorMessage(String... errors) {
        sendSlackMessage(SLACK_ERROR_URL, joinWithSeparator(errors));
    }

    public void sendErrorMessage(Exception e) {
        sendErrorMessage(Utils.toError(e));
    }

    public void sendInfoMessage(String text) {
        sendSlackMessage(SLACK_INFO_URL, text);
    }

    private void sendSlackMessage(String url, String text) {
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Type", "application/json");

            HttpRequestStruct httpRequestStruct = HttpRequestStruct.builder()
                    .url(url)
                    .body("""
                            {
                            "text": "%s"
                            }
                            """.formatted(text))
                    .headers(httpHeaders)
                    .build();
            try {
                HttpEntity<String> request = new HttpEntity<>(httpRequestStruct.getBody().replaceAll("\n", ""),
                        httpRequestStruct.getHeaders());
                restTemplate.postForObject(httpRequestStruct.getUrl(), request, String.class);
            } catch (HttpServerErrorException.InternalServerError | HttpServerErrorException.GatewayTimeout e) {
                log.error(e.getMessage());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void sendForm(FormBodyRequest formBodyRequest) {

        String body = """
                name: %s
                email: %s
                createdAt: %s
                header: %s
                description: %s
                """.formatted(formBodyRequest.name(),
                formBodyRequest.email(),
                LocalDateTime.now(),
                formBodyRequest.header(),
                formBodyRequest.description());
        String url = TELEGRAM_BOT_URL.formatted(TELEGRAM_BOT_TOKEN, TELEGRAM_CHAT_ID, "\"" + body + "\"");

        try {
            restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private String joinWithSeparator(String... strings) {
        return String.join("\n----------------------------------------------------------\n", strings);
    }
}
