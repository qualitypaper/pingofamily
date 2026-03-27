package com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingScore;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleStatistics;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample.TrainingExampleMistakeData;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.TrainingMistake;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class DefaultTrainingScore implements TrainingScoreFactory {
  @Override
  public TrainingScore getTrainingScoreAndConvert(List<TrainingExampleStatistics> statistics) {
    return calculateScore(new ArrayList<>(statistics.stream().map(TrainingExampleMistakeData::from).toList()));
  }

  @Override
  public TrainingScore getTrainingScore(List<TrainingExampleMistakeData> result) {
    return calculateScore(result);
  }

  @Override
  public TrainingScore getOverallTrainingScore(List<TrainingMistake> result) {
    double score = result.stream()
            .mapToDouble(e -> e.trainingExampleScore().val())
            .average()
            .orElse(0.0);

    return new TrainingScore(score);
  }


  private TrainingScore calculateScore(List<TrainingExampleMistakeData> mistakes) {
    double score = 0;
    TrainingExampleMistakeData[] sorted = mistakes.stream()
            .sorted(Comparator.comparingLong(e -> e.timestamp().toEpochSecond(ZoneOffset.UTC)))
            .toArray(TrainingExampleMistakeData[]::new);

    for (int i = 0; i < mistakes.size(); i++) {
      var mistake = sorted[i];
      double temp;

      if (!mistake.skipped() && !mistake.hint()) {
        temp = 10;
      } else if (!mistake.skipped()) {
        temp = 7;
      } else if (!mistake.hint()) {
        temp = 3;
      } else {
        temp = 0;
      }
      score += temp / (1 + Math.log1p(i));
    }

    score = Math.clamp(score / mistakes.size(), 0, 10);
    return new TrainingScore(score);
  }
}
