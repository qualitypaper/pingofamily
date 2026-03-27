package com.qualitypaper.fluentfusion.controller.dto.response.vocabulary;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WordTranslationResponse(
        Long id,
        WordResponse wordTo,
        WordResponse wordFrom
) {
}
