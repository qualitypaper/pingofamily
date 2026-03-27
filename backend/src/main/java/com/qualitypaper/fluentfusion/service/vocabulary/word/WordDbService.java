package com.qualitypaper.fluentfusion.service.vocabulary.word;

import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.Training;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExample;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleData;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordExampleTranslation;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import com.qualitypaper.fluentfusion.repository.*;
import com.qualitypaper.fluentfusion.repository.userVocabulary.UserVocabularyRepository;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.UserVocabularyDbService;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.statistics.UserVocabularyStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WordDbService {
  private final UserVocabularyStatisticsRepository userVocabularyStatisticsRepository;
  private final WordTranslationRepository wordTranslationRepository;

  private final WordService wordService;
  private final WordTranslationService wordTranslationService;
  private final UserVocabularyDbService userVocabularyDbService;
  private final UserVocabularyRepository userVocabularyRepository;
  private final WordExampleTranslationRepository wordExampleTranslationRepository;
  private final WordExampleRepository wordExampleRepository;
  private final TrainingExampleDataRepository trainingExampleDataRepository;
  private final TrainingExampleRepository trainingExampleRepository;
  private final TrainingRepository trainingRepository;

  @Async
  @Transactional
  public void deleteWord(long id) {
    Optional<Word> word = wordService.findById(id);

    if (word.isEmpty())
      return;

    List<WordTranslation> list = wordTranslationService.getAllByWord(word.get());

    for (WordTranslation wordTranslation : list) {

      List<UserVocabulary> temp = userVocabularyRepository.findAllByWordTranslation(wordTranslation);
      for (UserVocabulary userVocabulary : temp) {
        userVocabularyDbService.delete(userVocabulary);
        log.info("UserVocabulary with id: {} deleted", userVocabulary.getId());
      }

      List<WordExampleTranslation> examples = wordExampleTranslationRepository.getAllByWordTranslation(wordTranslation);
      for (WordExampleTranslation example : examples) {
        wordExampleTranslationRepository.delete(example);
        log.info("WordExampleTranslation with id: {} deleted", example.getId());
      }

      List<TrainingExampleData> trainingExampleDataList = trainingExampleDataRepository.findAllByWordTranslation(wordTranslation);
      for (TrainingExampleData trainingExampleData : trainingExampleDataList) {
        List<TrainingExample> trainingExamples = trainingExampleRepository.findAllByTrainingExampleData(trainingExampleData);
        Collector<TrainingExample, ?, Map<Training, Long>> grouper = Collectors.groupingBy(TrainingExample::getTraining, Collectors.counting());
        Map<Training, Long> trainingCount = trainingExamples.stream().collect(grouper);
        for (Training training : trainingCount.keySet()) {
          userVocabularyRepository.findAllByNextTraining(training).forEach(userVocabularyDbService::delete);
          userVocabularyStatisticsService.findByTraining(training).ifPresent(userVocabularyStatisticsRepository::delete);
          trainingRepository.delete(training);
        }

        trainingExampleDataRepository.delete(trainingExampleData);
        log.info("TrainingExampleData with id: {} deleted", trainingExampleData.getId());
      }


      wordTranslationRepository.delete(wordTranslation);
      log.info("Deleted wordTranslation with id: {}", wordTranslation.getId());
    }

    wordExampleRepository.findAllByWord(word.get()).forEach(wordExample -> {
      wordExampleRepository.delete(wordExample);
      log.info("WordExample with id: {} deleted", wordExample.getId());
    });

    wordService.delete(word.get());

    log.info("Word with id: {} deleted", id);
  }

  private final UserVocabularyStatisticsService userVocabularyStatisticsService;
}
