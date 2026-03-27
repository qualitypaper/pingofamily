package com.qualitypaper.fluentfusion.controller.admin;

import com.qualitypaper.fluentfusion.controller.dto.request.MoveWordsRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.RegenerateConjugationsRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.RemoveWordPartRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.UpdateWordRequest;
import com.qualitypaper.fluentfusion.service.typealiase.MapSB;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.UserVocabularyAdminService;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.UserVocabularyScriptService;
import com.qualitypaper.fluentfusion.util.HttpUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/user-vocabulary")
public class UserVocabularyAdminController {


  private final UserVocabularyAdminService userVocabularyAdminService;
  private final UserVocabularyScriptService userVocabularyScriptService;

  public UserVocabularyAdminController(UserVocabularyAdminService userVocabularyAdminService, UserVocabularyScriptService userVocabularyScriptService) {
    this.userVocabularyAdminService = userVocabularyAdminService;
    this.userVocabularyScriptService = userVocabularyScriptService;
  }

  @PostMapping("/move-words")
  public ResponseEntity<MapSB> moveWords(@RequestBody MoveWordsRequest moveWordsRequest) {
    userVocabularyScriptService.moveWords(moveWordsRequest);
    return ResponseEntity.ok(HttpUtils.successResponse());
  }

  @PutMapping("/update-word")
  public void updateWord(@RequestBody UpdateWordRequest updateWordRequest) {
    userVocabularyAdminService.updateWord(updateWordRequest, SecurityContextHolder.getContext());
  }

  @PutMapping("/update-words")
  public void updateWords(@RequestBody List<UpdateWordRequest> updateWordsRequest) {
    userVocabularyAdminService.updateWords(updateWordsRequest);
  }

  @GetMapping("/regenerate-sounds/{userVocabularyId}")
  public ResponseEntity<MapSB> regenerateSounds(@PathVariable Long userVocabularyId, @RequestParam Boolean example) {
    userVocabularyAdminService.regenerateSound(userVocabularyId, example);
    return ResponseEntity.ok(HttpUtils.successResponse());
  }

  @PostMapping("/change-word-image/{userVocabularyId}")
  public ResponseEntity<MapSB> changeWordImage(@PathVariable Long userVocabularyId, @RequestParam("file") MultipartFile file) {
    userVocabularyAdminService.changeWordImage(userVocabularyId, file);
    return ResponseEntity.ok(HttpUtils.successResponse());
  }

  @GetMapping("/reload-word-image/{userVocabularyId}")
  public ResponseEntity<MapSB> reloadWordImage(@PathVariable Long userVocabularyId) {
    userVocabularyAdminService.reloadWordImage(userVocabularyId);
    return ResponseEntity.ok(HttpUtils.successResponse());
  }

  @GetMapping("/regenerate-images-for-all-words/{vocabularyGroupId}")
  public ResponseEntity<MapSB> regenerateImagesForAllWords(@PathVariable Long vocabularyGroupId) {
    userVocabularyAdminService.regenerateImagesForAllWords(vocabularyGroupId);
    return ResponseEntity.ok(HttpUtils.successResponse());
  }

  @PostMapping("/regenerate-conjugations")
  public ResponseEntity<MapSB> regenerateConjugations(@RequestBody RegenerateConjugationsRequest regenerateConjugationsRequest) {
    userVocabularyAdminService.regenerateConjugations(regenerateConjugationsRequest.userVocabularyId());
    return ResponseEntity.ok(HttpUtils.successResponse());
  }

  @PostMapping("/regenerate-conjugations-plural")
  public ResponseEntity<MapSB> regenerateConjugations(@RequestBody List<Long> userVocabularyIds) {
    userVocabularyAdminService.regenerateConjugations(userVocabularyIds);

    return ResponseEntity.ok(HttpUtils.successResponse());
  }

  @PostMapping("/remove-word-part")
  public ResponseEntity<MapSB> removeWordPart(@RequestBody RemoveWordPartRequest removeWordPartRequest) {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    userVocabularyAdminService.removeWordPartFromWords(removeWordPartRequest, securityContext);
    return ResponseEntity.ok(HttpUtils.successResponse());
  }
}