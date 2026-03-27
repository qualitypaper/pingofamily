package com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.wordPrioritization.dataset;

import com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.wordPrioritization.NormalizationUtil;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingScore.TrainingScore;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.LagTime;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.TrainingHistory;
import com.qualitypaper.fluentfusion.util.FileUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DatasetParser {
  // 1 hour
  private static final double MIN_LAG = LagTime.MIN_VALUE.toHours();
  private static final double MAX_LAG = LagTime.MAX_VALUE.toHours();
  private static final double MIN_CORRECTNESS = TrainingScore.MIN_VALUE;
  private static final double MAX_CORRECTNESS = TrainingScore.MAX_VALUE;
  private static final double MIN_HISTORY = TrainingHistory.MIN_VALUE;
  private static final double MAX_HISTORY = TrainingHistory.MAX_VALUE;

  private DatasetParser() {
  }

  public static NormalizedDataset fromCsv(String filepath) {

    List<Map<String, String>> rawData;
    try {
      rawData = FileUtils.readCsvMap(filepath);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }

    int n = rawData.size();

    double[] targets = new double[n];
    double[] lagTimes = new double[n];
    double[] history = new double[n];
    double[] correctness = new double[n];


    for (int i = 0; i < n; i++) {
      Map<String, String> map = rawData.get(i);
      targets[i] = Double.parseDouble(map.get("p_recall"));

      // delta is in seconds
      long delta = Long.parseLong(map.get("delta"));
      // to days
      lagTimes[i] = Math.clamp(delta / 3600.0, MIN_LAG, MAX_LAG);
      double corr = Math.clamp(Double.parseDouble(map.get("correctness")) * 10, MIN_CORRECTNESS, MAX_CORRECTNESS);
      double his = Math.clamp(Double.parseDouble(map.get("history_seen")), MIN_HISTORY, MAX_HISTORY);
      correctness[i] = corr;
      history[i] = his;
    }

    NormalizationUtil.normalize(history, MIN_HISTORY, MAX_HISTORY);
    NormalizationUtil.normalize(correctness, MIN_CORRECTNESS, MAX_CORRECTNESS);
//    NormalizationUtil.normalize(lagTimes, MIN_LAG, MAX_LAG);

    double[][] variables = new double[n][2];
    for (int i = 0; i < variables.length; i++) {
     variables[i][0] = correctness[i];
     variables[i][1] = history[i];
    }

    return new NormalizedDataset(targets, variables, lagTimes);
  }

}
