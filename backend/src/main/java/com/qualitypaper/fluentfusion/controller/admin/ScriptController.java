package com.qualitypaper.fluentfusion.controller.admin;

import com.qualitypaper.fluentfusion.controller.dto.request.GeneratePredefinedRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.ReverseVocabularyGroupRequest;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.Desc;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TrainingType;
import com.qualitypaper.fluentfusion.repository.DescRepository;
import com.qualitypaper.fluentfusion.service.db.DbService;
import com.qualitypaper.fluentfusion.service.db.RedisService;
import com.qualitypaper.fluentfusion.service.script.TrainingCountUpdater;
import com.qualitypaper.fluentfusion.service.script.WordsAddingScript;
import com.qualitypaper.fluentfusion.service.typealiase.MapSB;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.UserVocabularyScriptService;
import com.qualitypaper.fluentfusion.util.HttpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scripts")
public class ScriptController {

  private final RedisService redisService;
  private final UserVocabularyScriptService userVocabularyScriptService;
  private final WordsAddingScript wordsAddingScript;
  private final TrainingCountUpdater trainingCountUpdater;
  private final DbService dbService;
  private final DescRepository descRepository;

  @PostMapping("/copyDescriptions")
  public void copyDescriptions() {
    List<Map<String, Object>> all = dbService.select("word_desc", List.of("id", "created_at", "_desc"));

    for (Map<String, Object> desc : all) {
      Timestamp createdAt = (Timestamp) desc.get("created_at");
      String description = desc.get("_desc").toString();
      LocalDateTime newCreatedAt = LocalDateTime.ofInstant(createdAt.toInstant(), ZoneId.of("UTC"));

      Desc newDesc = Desc.builder()
              .createdAt(newCreatedAt)
              .description(description)
              .build();
      descRepository.save(newDesc);
    }
  }

  @PutMapping("/update-training-count")
  public void updateTrainingCount() {
    trainingCountUpdater.updateTrainingCount();
  }

  @PostMapping("/generate-predefined")
  public String generatePredefinedGroup(@RequestBody GeneratePredefinedRequest generatePredefinedRequest) {
    wordsAddingScript.launch(
            generatePredefinedRequest
    );

    return "Script was successfully launched, watch logs to see results";
  }

  @PostMapping("/trimCacheKeys")
  public ResponseEntity<MapSB> trimCacheKeys() {
    redisService.trimAllKeys();
    return ResponseEntity.ok(HttpUtils.successResponse());
  }

  @PostMapping("/reverseVocabularyGroup")
  public void reverseVocabularyGroup(@RequestBody ReverseVocabularyGroupRequest request) {
    wordsAddingScript.reversePredefinedVocabularyGroup(request);
  }

  @PostMapping("/fixTraining")
  public void fixTraining(@RequestBody FixTrainingRequest request) {
    userVocabularyScriptService.fixTraining(request.duplicateType(), request.correctType());
  }

  @PostMapping("/fixSound")
  public void fixSounds() {
    userVocabularyScriptService.fixSounds();
  }

  @PostMapping("/removeNullReferencedTrainingExamples")
  public void removeNullReferencedTrainingExamples() {
    userVocabularyScriptService.removeNullReferencedTrainingExamples();
  }


  public record FixTrainingRequest(TrainingType duplicateType, TrainingType correctType) {
  }

}
