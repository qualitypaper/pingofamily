package com.qualitypaper.fluentfusion.controller.dto.response.auth;

import com.qualitypaper.fluentfusion.model.Language;

public record SettingsResponse(Language interfaceLanguage, int wordsPerPage) {
}
