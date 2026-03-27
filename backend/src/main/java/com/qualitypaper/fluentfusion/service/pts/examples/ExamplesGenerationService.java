package com.qualitypaper.fluentfusion.service.pts.examples;

import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TrainingType;
import com.qualitypaper.fluentfusion.service.pts.Util;
import com.qualitypaper.fluentfusion.service.pts.openai.OpenAiCompletionService;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.ExampleTranslationStruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamplesGenerationService {

  private final OpenAiCompletionService openAiCompletionService;

  public List<ExamplesResponse> generateTrainingExamples(ExampleTranslationStruct examplesGeneration, TrainingType trainingType) {
    String prompt = ExamplesJsonService.getPrompt(examplesGeneration, trainingType);
    return processPrompt(prompt);
  }

  public List<ExamplesResponse> generateExamples(ExampleTranslationStruct examplesGeneration) {
    String prompt = ExamplesJsonService.getJsonObjectForExampleTranslation(examplesGeneration);
    return processPrompt(prompt);
  }

  private List<ExamplesResponse> processPrompt(String prompt) {

    for (int i = 0; i < 3; i++) {
      try {
        String completion = openAiCompletionService.createCompletion(prompt, new ArrayList<>());

        if (completion != null && !completion.contains("None") && !completion.contains("null")) {
          Map<String, String> contentMap = Util.findJsonObject(completion);

          return contentMap.entrySet().stream()
                  .map(e -> new ExamplesResponse(
                                  Language.valueOf(e.getKey().toUpperCase()),
                                  saveUmlauts(e.getValue())
                          )
                  )
                  .toList();
        }
      } catch (Exception e) {
        log.error(e.getMessage());
        log.error("Retrying chatgpt completion, as long as previous one failed, in method generateExamples()");
      }
    }

    return Collections.emptyList();
  }

  public static String saveUmlauts(String s) {
    return s.replace("Ã¤", "ä")
            .replace("Ã¶", "ö")
            .replace("Ã¼", "ü")
            .replace("Ã„", "Ä")
            .replace("Ã–", "Ö")
            .replace("Ãœ", "Ü")
            .replace("ÃŸ", "ß");
  }
}
