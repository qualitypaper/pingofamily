package com.qualitypaper.fluentfusion.controller.dto.response.vocabulary;

import java.util.Map;

public record GermanNounConjugationResponse(
        String gender,
        Map<String, Map<String, String>> mappings
) {
}
