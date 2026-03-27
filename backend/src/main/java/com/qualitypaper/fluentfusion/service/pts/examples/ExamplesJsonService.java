package com.qualitypaper.fluentfusion.service.pts.examples;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TrainingType;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample.TrainingExampleService;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.ExampleTranslationStruct;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.TranslationDirection;

class ExamplesJsonService {

  private static final String EXAMPLES_RESPONSE_FORMAT = """
          Please return the examples in json format, use languages as keys and sentences as values.
          Do not add any additional text to the response.
          """;

  public static String getPrompt(ExampleTranslationStruct trainingGenerationStruct, TrainingType trainingType) {
    if (TrainingExampleService.getTranslationDirection(trainingType).equals(TranslationDirection.REVERSED)) {
      trainingGenerationStruct = trainingGenerationStruct.reverse();
    }

    return switch (trainingType) {
      case SENTENCE_AUDIO, SENTENCE_TYPE, PHRASE_CONSTRUCTION, PHRASE_CONSTRUCTION_REVERSED ->
              getJsonObjectForPhraseConstruction(trainingGenerationStruct);
      case COMPLETE_EMPTY_SPACES -> getJsonObjectForEmptySpaces(trainingGenerationStruct);
      case AUDIO, TRANSLATION -> "";
    };
  }

  private static String getJsonObjectForPhraseConstruction(ExampleTranslationStruct phraseGenerationStruct) {
    return String.format("""
                    Generate a sentence of a %s difficulty,
                    so that it directly includes the word %s,
                    with translation %s
                    and its length should not be more than 15 words, Use the %s
                    language and its translation into %s,
                    %s""",
            phraseGenerationStruct.getDifficulty(),
            phraseGenerationStruct.getWord(),
            phraseGenerationStruct.getTranslation(),
            phraseGenerationStruct.getSourceLanguage(),
            phraseGenerationStruct.getTargetLanguage(),
            EXAMPLES_RESPONSE_FORMAT);
  }

  private static String getJsonObjectForEmptySpaces(ExampleTranslationStruct trainingPhraseGenerationStruct) {
    return String.format("""
                    Generate a sentence for the language learning program. The sentence should be of easy difficulty,
                    this word must perfectly fit in the sentence context,
                    and this word is %s
                    with translation %s,
                    Use the %s
                    language and its translation into %s,
                    %s""",
            trainingPhraseGenerationStruct.getWord(),
            trainingPhraseGenerationStruct.getTranslation(),
            trainingPhraseGenerationStruct.getSourceLanguage(),
            trainingPhraseGenerationStruct.getTargetLanguage(),
            EXAMPLES_RESPONSE_FORMAT);
  }

  public static String getJsonObjectForExampleTranslation(ExampleTranslationStruct exampleTranslationStruct) {
    return String.format("""
                    Generate a unique %s level %s sentence using the word
                    '%s' not the synonym, translate it into %s with
                    translation: %s, the word must be used in
                    %s in both languages and place the translated sentence next.
                    The resulting sentences should be geared towards individuals learning: %s
                    %s""",
            exampleTranslationStruct.getDifficulty(),
            exampleTranslationStruct.getSourceLanguage(),
            exampleTranslationStruct.getWord(),
            exampleTranslationStruct.getTargetLanguage(),
            exampleTranslationStruct.getTranslation(),
            exampleTranslationStruct.getPartOfSpeech().name().toLowerCase(),
            exampleTranslationStruct.getTargetLanguage(),
            EXAMPLES_RESPONSE_FORMAT);
  }

}