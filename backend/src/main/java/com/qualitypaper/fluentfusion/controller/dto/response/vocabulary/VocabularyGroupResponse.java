package com.qualitypaper.fluentfusion.controller.dto.response.vocabulary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroupType;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VocabularyGroupResponse(long groupId, String name,
                                      String imageUrl, long vocabularyId,
                                      Language learningLanguage, Language nativeLanguage,
                                      VocabularyGroupType type, Boolean activated) {

}
