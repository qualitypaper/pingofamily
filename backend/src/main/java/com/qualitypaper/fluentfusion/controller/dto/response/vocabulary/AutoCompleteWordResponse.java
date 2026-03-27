package com.qualitypaper.fluentfusion.controller.dto.response.vocabulary;


import java.util.List;

public record AutoCompleteWordResponse(List<PossibleTranslationResponse> possibleTranslations) {
}
