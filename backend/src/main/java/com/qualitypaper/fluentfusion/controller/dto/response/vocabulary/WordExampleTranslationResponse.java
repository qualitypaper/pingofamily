package com.qualitypaper.fluentfusion.controller.dto.response.vocabulary;

public record WordExampleTranslationResponse(
        Long id,
        String example,
        String exampleTranslation,
        String soundUrl,
        Long wordId
) {
}
