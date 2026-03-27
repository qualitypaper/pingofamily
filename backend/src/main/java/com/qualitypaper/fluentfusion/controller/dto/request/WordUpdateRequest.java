package com.qualitypaper.fluentfusion.controller.dto.request;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;

public record WordUpdateRequest(String word, PartOfSpeech partOfSpeech, String soundUrl, String imageUrl) {
}
