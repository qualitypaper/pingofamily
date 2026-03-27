package com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.wordPrioritization.test;


import com.qualitypaper.fluentfusion.buffers.Weights;
import com.qualitypaper.fluentfusion.config.ApplicationConfig;
import com.qualitypaper.fluentfusion.service.serialization.WeightsSerializationService;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.wordPrioritization.PrioritizationModel;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingScore.TrainingScore;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.LagTime;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.TrainingHistory;
import com.qualitypaper.fluentfusion.util.types.Pair;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class WordPrioritizationModelTest {

  private final PrioritizationModel model = new PrioritizationModel();

  private Weights weights;

  private final WeightsSerializationService weightsSerializationService;

  public WordPrioritizationModelTest(WeightsSerializationService weightsSerializationService) {
    this.weightsSerializationService = weightsSerializationService;
  }

  @PostConstruct
  public void init() throws IOException {
    this.weights = weightsSerializationService.loadDefaultWeights();
  }

  public List<double[]> getRandomlySpacedTrainingResult(int trainingCount) {
    List<double[]> result = new ArrayList<>();

    LagTime lagTime = LagTime.random(Duration.ofHours(100));
    TrainingScore score = TrainingScore.random();
    TrainingHistory history = TrainingHistory.random();

    for (int i = 0; i < trainingCount; i++) {
      double[] W = WeightsSerializationService.toArray(this.weights);
      double[] X = new double[]{score.normalize(), history.normalize()};

      Pair<Double, Double> predict = model.predict(
              X,
              lagTime.val().toHours(),
              W
      );
      float expected = ApplicationConfig.RANDOM.nextFloat(predict.second().floatValue() - 0.5f, 1.0f);

      double[] newWeights = model.train(new double[][]{X}, new double[]{lagTime.normalize()}, new double[]{expected}, W);

      result.add(new double[]{lagTime.val().toHours(), predict.first(), predict.second(), expected,});
      this.weights = WeightsSerializationService.fromArray(newWeights);

      lagTime = LagTime.random(Duration.ofHours(24));
      score = TrainingScore.random();
//      history = new TrainingHistory(history.trainingCount() + 1);
    }

    return result;
  }

}
