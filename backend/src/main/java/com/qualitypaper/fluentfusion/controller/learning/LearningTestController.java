package com.qualitypaper.fluentfusion.controller.learning;

import com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.wordPrioritization.test.WordPrioritizationModelTest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/learning-test")
@RequiredArgsConstructor
public class LearningTestController {

  private final WordPrioritizationModelTest modelTest;

  @GetMapping("/random")
  public List<double[]> getRandomlySpacedTrainings(@RequestParam(required = false) Integer trainingCount) {
    return modelTest.getRandomlySpacedTrainingResult(trainingCount != null ? trainingCount : 7);
  }
}
