package com.qualitypaper.fluentfusion.controller.dto.response.vocabulary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qualitypaper.fluentfusion.model.Language;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VocabularyResponse(Long id, Language learningLanguage, Language nativeLanguage,
                                 List<VocabularyGroupResponse> vocabularyGroupList, String error) {
}
