package com.qualitypaper.fluentfusion.controller.dto.request;

import java.util.List;

public record RemoveWordPartRequest(String needle, List<Long> userVocabularyIds) {
}
