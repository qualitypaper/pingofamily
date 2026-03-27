package com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.statistics;

import com.qualitypaper.fluentfusion.model.vocabulary.WordStatistics;
import com.qualitypaper.fluentfusion.repository.WordStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WordStatisticsService {

  private final WordStatisticsRepository wordStatisticsRepository;

  public WordStatistics createAndSave() {
    var ws = WordStatistics.builder().createdAt(System.currentTimeMillis()).build();
    wordStatisticsRepository.save(ws);
    return ws;
  }

  public void save(WordStatistics ws) {
    wordStatisticsRepository.save(ws);
  }


  public void initialize(WordStatistics wordStatistics) {
    wordStatistics.setCreatedAt(System.currentTimeMillis());
    wordStatistics.setLastMonthTrainingCount(0);
    wordStatistics.setLastWeekTrainingCount(0);
    wordStatisticsRepository.save(wordStatistics);
  }
}