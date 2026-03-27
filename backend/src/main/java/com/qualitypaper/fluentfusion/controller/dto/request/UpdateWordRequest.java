package com.qualitypaper.fluentfusion.controller.dto.request;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;

import java.util.List;

public record UpdateWordRequest(Long userVocabularyId, String word, String wordTranslation, PartOfSpeech partOfSpeech,
                                String gender,
                                String example, String exampleTranslation, String description,
                                String descriptionTranslation, List<String> synonyms) {

}
