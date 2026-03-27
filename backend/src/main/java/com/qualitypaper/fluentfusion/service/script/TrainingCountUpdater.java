package com.qualitypaper.fluentfusion.service.script;

import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabularyStatistics;
import com.qualitypaper.fluentfusion.model.vocabulary.WordStatistics;
import com.qualitypaper.fluentfusion.repository.userVocabulary.UserVocabularyRepository;
import com.qualitypaper.fluentfusion.service.db.DbService;
import com.qualitypaper.fluentfusion.service.db.types.Compare;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.statistics.WordStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TrainingCountUpdater {

  private final WordStatisticsService wordStatisticsService;
  private final DbService dbService;
  private final UserVocabularyRepository userVocabularyRepository;

  public void updateTrainingCount() {

    for (int i = 0; ; i++) {
      List<UserVocabulary> slice = userVocabularyRepository.findAllWithPagination((i + 1) * 100, i * 100);
      if (slice.isEmpty()) {
        break;
      }

      update(slice);
    }
  }

  public void update(List<UserVocabulary> userVocabularyInfos) {
    Map<Long, Boolean> visited = new HashMap<>();


    for (UserVocabulary vocabularyTrainingStatistics : userVocabularyInfos) {
      Long userVocabularyId = vocabularyTrainingStatistics.getId();
      WordStatistics wordStatistics = vocabularyTrainingStatistics.getWordStatistics();
      List<UserVocabularyStatistics> trainingStatistics = vocabularyTrainingStatistics.getTrainingStatistics();

      if (visited.get(userVocabularyId) == null) {
        visited.put(userVocabularyId, true);
        WordStatistics temp = null;
        if (wordStatistics == null) {
          temp = wordStatisticsService.createAndSave();
          dbService.update(UserVocabulary.class, Map.of("word_statistics_id", temp.getId()),
                  List.of(Compare.eq("id", userVocabularyId)));
        }

        if (temp != null) {
          temp.setLastWeekTrainingCount(0);
          temp.setLastMonthTrainingCount(0);
          wordStatisticsService.save(temp);
          wordStatistics = temp;
        }
      }


      long lastWeekTrainingCount = 0;
      long lastMonthTrainingCount = 0;

      for (UserVocabularyStatistics trainingStatistic : trainingStatistics) {
        if (trainingStatistic.getTrainingTime().isAfter(LocalDateTime.now().minusDays(7))) {
          lastWeekTrainingCount++;
        }
        if (trainingStatistic.getTrainingTime().isAfter(LocalDateTime.now().minusMonths(1))) {
          lastMonthTrainingCount++;
        }
      }

      if (wordStatistics == null) return;

      dbService.update(WordStatistics.class,
              Map.of(
                      "last_month_training_count", lastMonthTrainingCount,
                      "last_week_training_count", lastWeekTrainingCount
              ),
              List.of(Compare.eq("id", wordStatistics.getId())));

    }
  }
}
