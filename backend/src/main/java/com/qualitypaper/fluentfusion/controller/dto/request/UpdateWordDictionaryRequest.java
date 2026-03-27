package com.qualitypaper.fluentfusion.controller.dto.request;

import java.util.List;

public record UpdateWordDictionaryRequest(List<String> synonyms, String desc) {
}
