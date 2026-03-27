package com.qualitypaper.fluentfusion.service.vocabulary.learning;

import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.WordStatistics;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleStatistics;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.wordPrioritization.PrioritizationModel;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample.TrainingExampleResultData;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample.TrainingExampleService;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample.TrainingExampleStatisticsService;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingScore.TrainingScore;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingScore.TrainingScoreFactory;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.LagTime;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.TrainingHistory;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.TrainingMistake;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.TrainingMistakeWithScore;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.UserVocabularyService;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.statistics.WordStatisticsService;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabulary.VocabularyDbService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingCompleteService {
  private static final Logger log = LoggerFactory.getLogger(TrainingCompleteService.class);
  private final VocabularyDbService vocabularyDbService;
  private final TrainingExampleService trainingExampleService;
  private final TrainingExampleStatisticsService trainingExampleStatisticsService;
  private final TrainingScoreFactory trainingScoreFactory;
  private final PrioritizationModel prioritizationModel;
  private final WordStatisticsService wordStatisticsService;

  @Transactional
  protected TrainingMistakeWithScore chargePriorityByWord(UserVocabulary userVocabulary, List<TrainingExampleResultData> trainingExampleResultData) {
    updateTrainingExamples(trainingExampleResultData);
    WordStatistics wordStatistics = userVocabulary.getWordStatistics();

    List<TrainingMistake> trainingMistakes = trainingExampleResultData.stream()
            .map(e -> new TrainingMistake(e, trainingScoreFactory.getTrainingScore(e.mistakeData())))
            .toList();
    TrainingMistakeWithScore trainingMistakeWithScore = new TrainingMistakeWithScore(
            userVocabulary,
            trainingMistakes,
            trainingScoreFactory.getOverallTrainingScore(trainingMistakes)
    );
    updateVocabulary(userVocabulary.getVocabulary(), trainingMistakeWithScore);

    double[] newWeights = updateWeights(userVocabulary, wordStatistics);

    log.info("Updated weights for userVocabulary: {}, newWeights: {}", userVocabulary.getId(), Arrays.toString(newWeights));

    wordStatistics.getWeights().clear();
    for (double newWeight : newWeights) {
      wordStatistics.getWeights().add(newWeight);
    }
    wordStatistics.setAverageTrainingScore(
            (wordStatistics.getAverageTrainingScore() * wordStatistics.getTotalTrainingCount()
                    + trainingMistakeWithScore.trainingScore().val()) / (wordStatistics.getTotalTrainingCount() + 1)
    );
    wordStatistics.setTotalTrainingCount(wordStatistics.getTotalTrainingCount() + 1);
    wordStatistics.setLastMonthTrainingCount(wordStatistics.getLastMonthTrainingCount() + 1);
    wordStatistics.setLastWeekTrainingCount(wordStatistics.getLastWeekTrainingCount() + 1);
    wordStatisticsService.save(wordStatistics);

    return trainingMistakeWithScore;
  }

  private double[] updateWeights(UserVocabulary userVocabulary, WordStatistics wordStatistics) {
    TrainingScore trainingScore = new TrainingScore(wordStatistics.getAverageTrainingScore());
    TrainingHistory trainingHistory = new TrainingHistory(wordStatistics.getTotalTrainingCount());
    LagTime lagTime = new LagTime(UserVocabularyService.timeFromLastTraining(userVocabulary));

    return prioritizationModel.train(
            new double[][]{{trainingScore.normalize(), trainingHistory.normalize()}},
            new double[]{trainingScore.normalize()},
            new double[]{lagTime.normalize()}
    );
  }

  private void updateVocabulary(Vocabulary vocabulary, TrainingMistakeWithScore trainingMistakeWithScore) {
    int trainingCount = vocabulary.getTrainingCount() == null ? 0 : vocabulary.getTrainingCount();
    double vocabularyAverageTrainingScore = vocabulary.getAverageTrainingScore() == null ? 0 : vocabulary.getAverageTrainingScore();
    double averageTrainingScore = (vocabularyAverageTrainingScore * trainingCount + trainingMistakeWithScore.trainingScore().val())
            / (trainingCount + 1);
    vocabulary.setAverageTrainingScore(Double.isNaN(averageTrainingScore) ? 0 : averageTrainingScore);
    vocabulary.setTrainingCount(trainingCount + 1);
    vocabularyDbService.save(vocabulary);
  }

  private void updateTrainingExamples(List<TrainingExampleResultData> list) {
    for (TrainingExampleResultData data : list) {
      List<TrainingExampleStatistics> statistics = data.mistakeData()
              .stream()
              .map(e -> trainingExampleStatisticsService.createAndSave(e.hint(), e.skipped(), data.trainingExample()))
              .toList();

      if (data.trainingExample().getTrainingExampleStatistics() == null) {
        data.trainingExample().setTrainingExampleStatistics(new ArrayList<>());
      }
      data.trainingExample().getTrainingExampleStatistics().addAll(statistics);
      trainingExampleService.save(data.trainingExample());
    }
  }

}
