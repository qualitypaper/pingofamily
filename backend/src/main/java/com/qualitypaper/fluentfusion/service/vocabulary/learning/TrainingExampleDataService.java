package com.qualitypaper.fluentfusion.service.vocabulary.learning;

import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.Training;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TrainingType;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleData;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleKeys;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordType;
import com.qualitypaper.fluentfusion.repository.TrainingExampleDataRepository;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample.TrainingExampleService;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.TranslationDirection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainingExampleDataService {

  private final TrainingExampleDataRepository trainingExampleDataRepository;
  private final TrainingExampleKeysService trainingExampleKeysService;

  public void save(TrainingExampleData trainingExampleData) {
    trainingExampleDataRepository.save(trainingExampleData);
  }

  public Optional<TrainingExampleData> exists(String sentence, String sentenceTranslation,
                                              String identifiedWord, WordTranslation wordTranslation,
                                              TrainingType trainingType) {
    return trainingExampleDataRepository
            .findTopBySentenceAndSentenceTranslationAndIdentifiedWordAndWordTranslationAndTrainingType(
                    sentence,
                    sentenceTranslation,
                    identifiedWord,
                    wordTranslation,
                    trainingType);
  }

  @Transactional
  public TrainingExampleData createAndSave(TrainingExampleParams trainingExampleParams) {
    Optional<TrainingExampleData> existingTrainingExample = exists(
            trainingExampleParams.sentence(),
            trainingExampleParams.sentenceTranslation(),
            trainingExampleParams.formattedSentence()[1],
            trainingExampleParams.wordTranslation(),
            trainingExampleParams.trainingType());

    if (existingTrainingExample.isPresent()) {
      return existingTrainingExample.get();
    }

    List<TrainingExampleKeys> wordsTranslation = generateWordsTranslation(
            trainingExampleParams.wordTranslation(),
            trainingExampleParams.sentence(),
            trainingExampleParams.trainingType()
    );

    TrainingExampleData trainingExampleData = TrainingExampleData.builder()
            .formattedString(trainingExampleParams.formattedSentence()[0])
            .identifiedWord(trainingExampleParams.formattedSentence()[1])
            .trainingType(trainingExampleParams.trainingType())
            .sentence(trainingExampleParams.sentence())
            .sentenceTranslation(trainingExampleParams.sentenceTranslation())
            .soundUrl(trainingExampleParams.soundUrl())
            .translationDirection(trainingExampleParams.translationDirection())
            .trainingType(trainingExampleParams.trainingType())
            .wordsTranslation(wordsTranslation)
            .wordTranslation(trainingExampleParams.wordTranslation())
            .build();

    return trainingExampleDataRepository.save(trainingExampleData);
  }

  private List<TrainingExampleKeys> generateWordsTranslation(WordTranslation wordTranslation, String sentence, TrainingType trainingType) {
    if (wordTranslation.getWordFrom().getWordType().equals(WordType.SENTENCE)
            || wordTranslation.getWordTo().getWordType().equals(WordType.SENTENCE)
            || !LearningService.needWordsTranslation.contains(trainingType))
      return Collections.emptyList();

    boolean isReversed = TrainingExampleService.isReversedTraining(trainingType);
    Language learningLanguage = isReversed
            ? wordTranslation.getWordTo().getLanguage()
            : wordTranslation.getWordFrom().getLanguage();
    Language nativeLanguage = isReversed
            ? wordTranslation.getWordFrom().getLanguage()
            : wordTranslation.getWordTo().getLanguage();

    return trainingExampleKeysService.createWordsTranslation(
            sentence,
            learningLanguage,
            nativeLanguage
    );

  }

  // we need learningLanguage, nativeLanguage because wordTo and wordFrom are not always complete entities (some parameters may be missing)
  public record TrainingExampleParams(WordTranslation wordTranslation,
                                      Training training,
                                      String sentence,
                                      String sentenceTranslation,
                                      String soundUrl,
                                      TranslationDirection translationDirection,
                                      TrainingType trainingType,
                                      String[] formattedSentence) {
  }
}
