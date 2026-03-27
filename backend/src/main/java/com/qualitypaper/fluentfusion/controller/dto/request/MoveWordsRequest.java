package com.qualitypaper.fluentfusion.controller.dto.request;

import java.util.List;

public record MoveWordsRequest(List<Long> userVocabularyIds, Long vocabularyGroupIdFrom, Long vocabularyGroupIdTo) {
}
