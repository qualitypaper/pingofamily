package com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary;

import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroup;
import com.qualitypaper.fluentfusion.model.vocabulary.WordStatistics;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordExampleTranslation;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.TrainingService;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.statistics.WordStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserVocabularyEntityService {

  private final TrainingService trainingService;
  private final UserVocabularyDbService userVocabularyDbService;
  private final WordStatisticsService wordStatisticsService;

  @Transactional
  public UserVocabulary create(WordTranslation wordTranslation, WordExampleTranslation wordExampleTranslation,
                               VocabularyGroup vocabularyGroup) {
    WordStatistics wordStatistics = wordStatisticsService.createAndSave();
    log.info("Created WordStatistics with id {}", wordStatistics.getId());

    UserVocabulary userVocabulary = UserVocabulary.builder()
            .vocabulary(vocabularyGroup.getVocabulary())
            .vocabularyGroup(vocabularyGroup)
            .wordStatistics(wordStatistics)
            .wordTranslation(wordTranslation)
            .wordExampleTranslation(wordExampleTranslation)
            .wellKnownWord(false)
            .isNew(true)
            .trainingStatistics(new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .build();

    log.info("Initialized UserVocabulary: {}", userVocabulary);
    userVocabularyDbService.save(userVocabulary);
    log.info("Created UserVocabulary with id {}", userVocabulary.getId());

    // load needed lazily initialized fields
    if (Hibernate.isInitialized(userVocabulary.getWordTranslation().getWordFrom().getWordDictionary())) {
      Hibernate.initialize(userVocabulary.getWordTranslation().getWordFrom().getWordDictionary());
    }
    trainingService.generateFirstTraining(userVocabulary);
//        userVocabulary.setNextTraining(training);

    return userVocabulary;
  }


}
