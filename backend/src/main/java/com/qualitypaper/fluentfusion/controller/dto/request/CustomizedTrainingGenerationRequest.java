package com.qualitypaper.fluentfusion.controller.dto.request;

import java.util.List;

public record CustomizedTrainingGenerationRequest(Long vocabularyGroupId,
                                                  List<Long> userVocabularyIds) {
}
