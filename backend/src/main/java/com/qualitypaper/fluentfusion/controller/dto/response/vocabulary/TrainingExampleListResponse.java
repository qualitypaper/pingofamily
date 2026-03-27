package com.qualitypaper.fluentfusion.controller.dto.response.vocabulary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TrainingType;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TrainingExampleListResponse(
        Long id,
        String sentence,
        String sentenceTranslation,
        String formattedString,
        String soundUrl,
        Map<String, List<String>> wordsTranslation,
        TrainingType trainingType,
        String identifiedWord
) {
}
