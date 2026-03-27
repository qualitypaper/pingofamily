package com.qualitypaper.fluentfusion.controller.dto.request;

import com.qualitypaper.fluentfusion.model.vocabulary.Difficulty;

public record RegenerateExamplesRequest(Long userVocabularyId, Long vocabularyGroupId, Difficulty difficulty) {
}
