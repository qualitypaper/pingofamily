package com.qualitypaper.fluentfusion.controller.dto.response.vocabulary;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WordDictionaryResponse(
        Long id,
        String description,
        String descriptionTranslation,
        List<String> synonyms,
        ConjugationResponse conjugation
) {
}
