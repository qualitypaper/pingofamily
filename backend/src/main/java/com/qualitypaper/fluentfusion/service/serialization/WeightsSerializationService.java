package com.qualitypaper.fluentfusion.service.serialization;

import com.qualitypaper.fluentfusion.buffers.Weights;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Component
public class WeightsSerializationService {

  @Value("${ai.weights.default.path}")
  private String defaultWeightsFilepath;

  public static Weights loadWeights(String filepath) throws IOException {
    Weights.Builder weightsBuilder = Weights.newBuilder();

    try (FileInputStream fis = new FileInputStream(filepath)) {
      weightsBuilder.mergeFrom(fis);
    }

    return weightsBuilder.build();
  }

  public static Weights fromArray(double[] W) {
    return Weights.newBuilder()
            .setCorrectnessWeight(W[0])
            .setExperienceWeight(W[1])
            .build();
  }

  public static double[] toArray(Weights weights) {
    return new double[] {weights.getCorrectnessWeight(), weights.getExperienceWeight()};
  }

  public Weights loadDefaultWeights() throws IOException {
    return loadWeights(defaultWeightsFilepath);
  }

  public void writeWeights(Weights weights, String filepath) throws IOException {
    weights.writeTo(new FileOutputStream(filepath));
  }

}
