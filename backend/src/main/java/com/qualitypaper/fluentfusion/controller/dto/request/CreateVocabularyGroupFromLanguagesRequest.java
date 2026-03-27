package com.qualitypaper.fluentfusion.controller.dto.request;

import com.qualitypaper.fluentfusion.model.Language;

public record CreateVocabularyGroupFromLanguagesRequest(Language learningLanguage, Language nativeLanguage,
                                                        String name) {
}
