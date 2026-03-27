package com.qualitypaper.fluentfusion.controller.dto.response.vocabulary;

import com.qualitypaper.fluentfusion.service.vocabulary.word.TranslationJson;

import java.util.List;

public record PossibleTranslationsResponse(LemmaResponse lemma, List<TranslationJson> possibleTranslations) {
}
