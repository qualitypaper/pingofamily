package com.qualitypaper.fluentfusion.controller.dto.response.vocabulary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WordResponse(
        Long id,
        String word,
        Language language,
        String imageUrl,
        String soundUrl,
        PartOfSpeech partOfSpeech,
        WordDictionaryResponse wordDictionary
) {
}
