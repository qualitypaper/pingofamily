package com.qualitypaper.fluentfusion.controller.dto.request;


import com.qualitypaper.fluentfusion.model.Language;

public record WordInformationRequest(String word, Language targetLanguage, Language sourceLanguage) {
}
