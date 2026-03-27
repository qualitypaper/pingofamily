package com.qualitypaper.fluentfusion.controller.learning;

import com.qualitypaper.fluentfusion.controller.dto.request.CompleteTrainingSessionRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.CustomizedTrainingGenerationRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.TrainingGenerationRequest;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.TrainingGenerationResponse;
import com.qualitypaper.fluentfusion.service.typealiase.MapSB;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.LearningService;
import com.qualitypaper.fluentfusion.util.HttpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/learning")
@RequiredArgsConstructor
public class LearningController {


  private final LearningService learningService;

  @PostMapping("/generate")
  public ResponseEntity<TrainingGenerationResponse> generateTrainingSession(@RequestBody TrainingGenerationRequest trainingGenerationRequest) {
    TrainingGenerationResponse body = learningService.generate(trainingGenerationRequest);
    System.out.println("Completed training generation");
    return ResponseEntity.ok(body);
  }

  @PostMapping("/generateCustomized")
  public ResponseEntity<TrainingGenerationResponse> generateTrainingSessionForSpecificWords(@RequestBody CustomizedTrainingGenerationRequest trainingGenerationRequest) {
    return ResponseEntity.ok(learningService.generateTrainingSessionForSpecificWords(trainingGenerationRequest));
  }

  @PostMapping("/complete")
  public MapSB completeTrainingSession(@RequestBody CompleteTrainingSessionRequest completeTrainingSessionRequest) {
    learningService.complete(completeTrainingSessionRequest, SecurityContextHolder.getContext());
    return HttpUtils.successResponse();
  }

  @GetMapping("/cancelTrainingSession")
  public MapSB cancelTrainingSession(@RequestParam Long trainingSessionId) {
    learningService.cancelTrainingSession(trainingSessionId);
    return HttpUtils.successResponse();
  }

}
