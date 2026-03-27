package com.qualitypaper.fluentfusion.controller.vocabulary;

import com.qualitypaper.fluentfusion.controller.dto.request.CreateFromPredefinedRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.CreateVocabularyGroupFromLanguagesRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.CreateVocabularyGroupRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.VocabularyGroupUpdateRequest;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.VocabularyGroupResponse;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.Difficulty;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroupType;
import com.qualitypaper.fluentfusion.service.typealiase.MapSB;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabularyGroup.VocabularyGroupService;
import com.qualitypaper.fluentfusion.util.HttpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vocabulary-group")
public class VocabularyGroupController {

  private final VocabularyGroupService vocabularyGroupService;

  @PostMapping("/create")
  public ResponseEntity<VocabularyGroupResponse> createVocabularyGroup(@RequestBody CreateVocabularyGroupRequest createVocabularyGroupRequest) {
    return ResponseEntity.ok(vocabularyGroupService.formatResponse(
            vocabularyGroupService.create(createVocabularyGroupRequest, VocabularyGroupType.USER_DEFINED, Difficulty.EASY)
    ));
  }

  @PostMapping("/create-predefined")
  public ResponseEntity<VocabularyGroupResponse> createPredefinedVocabularyGroup(@RequestBody CreateVocabularyGroupRequest createVocabularyGroupRequest) {
    return ResponseEntity.ok(
            vocabularyGroupService.formatResponse(
                    vocabularyGroupService.create(createVocabularyGroupRequest,
                            VocabularyGroupType.PREDEFINED, Difficulty.EASY)));
  }

  @PostMapping("/create-predefined-from-languages")
  public ResponseEntity<VocabularyGroupResponse> createPredefinedFromLanguages(@RequestBody CreateVocabularyGroupFromLanguagesRequest createVocabularyGroupFromLanguagesRequest) {
    return ResponseEntity.ok(
            vocabularyGroupService.formatResponse(
                    vocabularyGroupService.createPredefinedFromLanguages(createVocabularyGroupFromLanguagesRequest)
            )
    );
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<MapSB> deleteVocabularyGroup(@PathVariable Long id) {
    vocabularyGroupService.deleteAsync(id);
    return ResponseEntity.ok(HttpUtils.successResponse());
  }

  @GetMapping("/get-vocabulary-groups")
  public ResponseEntity<List<VocabularyGroupResponse>> getVocabularyGroups(@RequestParam Language learningLanguage,
                                                                           @RequestParam Language nativeLanguage) {
    return ResponseEntity.ok(vocabularyGroupService.getVocabularyGroups(learningLanguage, nativeLanguage));
  }

  @GetMapping("/get-suggested")
  public ResponseEntity<List<VocabularyGroupResponse>> getSuggestedVocabularyGroups(@RequestParam Language learningLanguage,
                                                                                    @RequestParam Language nativeLanguage) {
    return ResponseEntity.ok(vocabularyGroupService.getSuggestedVocabularyGroups(
            VocabularyGroupService.DEFAULT_SUGGESTED_VOCABULARY_GROUP_SELECT_LIMIT,
            learningLanguage, nativeLanguage));
  }

  @GetMapping("/get-all-suggested")
  public ResponseEntity<List<VocabularyGroupResponse>> getAllSuggestedVocabularyGroups(@RequestParam Language learningLanguage,
                                                                                       @RequestParam Language language) {
    return ResponseEntity.ok(vocabularyGroupService.getSuggestedVocabularyGroups(0, learningLanguage, language));
  }

  @PostMapping("/create-from-predefined")
  public ResponseEntity<Map<String, Long>> createFromPredefined(
          @RequestBody CreateFromPredefinedRequest createFromPredefinedRequest) throws ExecutionException, InterruptedException {
    return ResponseEntity.ok(
            Map.of(
                    "vocabularyGroupId",
                    vocabularyGroupService.createNewVocabularyGroupFromPredefined(createFromPredefinedRequest).get()
            )
    );
  }

  @GetMapping("/get-predefined")
  public ResponseEntity<List<VocabularyGroupResponse>> getPredefined(@RequestParam Language learningLanguage, @RequestParam Language nativeLanguage) {
    return ResponseEntity.ok(vocabularyGroupService.getAllPredefined(learningLanguage, nativeLanguage));
  }

  @DeleteMapping("/delete-predefined/{id}")
  public void deletePredefined(@PathVariable long id) {
    vocabularyGroupService.deletePredefined(id);
  }

  @PutMapping("/change")
  public ResponseEntity<MapSB> updateVocabularyGroup(@RequestBody VocabularyGroupUpdateRequest request) {
    vocabularyGroupService.updateVocabularyGroup(request);
    return ResponseEntity.ok(HttpUtils.successResponse());
  }
}
