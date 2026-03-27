package com.qualitypaper.fluentfusion.controller.dto.response.vocabulary;

import java.util.List;

public record GetWordsByVocabularyIdResponse(VocabularyGroupResponse vocabularyGroup, List<WordListResponse> words) {
}
