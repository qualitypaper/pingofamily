package com.qualitypaper.fluentfusion.controller.dto.request;

import com.qualitypaper.fluentfusion.model.Language;

public record GeneratePredefinedRequest(String filename, String vocabularyGroupName, Language sourceLanguage,
                                        Language targetLanguage, int offset, int limit) {
}
