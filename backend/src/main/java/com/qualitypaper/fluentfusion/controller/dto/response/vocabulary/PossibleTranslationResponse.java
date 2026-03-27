package com.qualitypaper.fluentfusion.controller.dto.response.vocabulary;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;

public record PossibleTranslationResponse(String translation, PartOfSpeech partOfSpeech) {
}
