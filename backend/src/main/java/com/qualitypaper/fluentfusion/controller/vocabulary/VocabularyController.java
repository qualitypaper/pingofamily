package com.qualitypaper.fluentfusion.controller.vocabulary;

import com.qualitypaper.fluentfusion.controller.dto.request.CreateUserVocabularyRequest;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.VocabularyResponse;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabulary.VocabularyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vocabulary")
public class VocabularyController {

  private final VocabularyService vocabularyService;

  @PostMapping(value = "/create", produces = "application/json")
  public ResponseEntity<VocabularyResponse> createVocabulary(@RequestBody CreateUserVocabularyRequest createUserVocabularyRequest) {
    return ResponseEntity.ok(vocabularyService.create(createUserVocabularyRequest));
  }

  @GetMapping("/get-all-vocabularies")
  public ResponseEntity<List<VocabularyResponse>> getAllVocabularies() {
    return ResponseEntity.ok(vocabularyService.getAllUserVocabularies());
  }

  @DeleteMapping("/delete-wrong-language")
  public void deleteWrongLanguage() {
    vocabularyService.deleteWrongLanguage();
  }

}
