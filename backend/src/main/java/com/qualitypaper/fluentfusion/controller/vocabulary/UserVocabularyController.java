package com.qualitypaper.fluentfusion.controller.vocabulary;

import com.qualitypaper.fluentfusion.controller.dto.request.AddTranslationRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.AddWordRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.RegenerateExamplesRequest;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.GetWordsByVocabularyIdResponse;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.UserVocabularyResponse;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.WordListResponse;
import com.qualitypaper.fluentfusion.repository.LearningSessionRepository;
import com.qualitypaper.fluentfusion.service.typealiase.MapSB;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.UserVocabularyService;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabulary.VocabularyService;
import com.qualitypaper.fluentfusion.util.HttpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-vocabulary")
public class UserVocabularyController {

  private final UserVocabularyService userVocabularyService;
  private final VocabularyService vocabularyService;

  @PostMapping("/add-word")
  public ResponseEntity<WordListResponse> addWord(@RequestBody AddWordRequest addWordRequest) {
    return ResponseEntity.ok(userVocabularyService.addNewWord(addWordRequest));
  }
  @PostMapping("/change-translation")
  public ResponseEntity<MapSB> changeTranslation(@RequestBody AddTranslationRequest addTranslationRequest) {
    userVocabularyService.changeTranslation(addTranslationRequest);
    return ResponseEntity.ok(HttpUtils.successResponse());
  }

  @GetMapping("/get-words/{vocabularyId}")
  public ResponseEntity<List<WordListResponse>> getWordsById(@PathVariable long vocabularyId) {
    return ResponseEntity.ok(userVocabularyService.getAllWordsByVocabularyId(vocabularyId));
  }

  @GetMapping("/get-word/{userVocabularyId}")
  public ResponseEntity<UserVocabularyResponse> getWordById(@PathVariable long userVocabularyId) {
    return ResponseEntity.ok(userVocabularyService.getWord(userVocabularyId));
  }

  @GetMapping("/get-words")
  public ResponseEntity<GetWordsByVocabularyIdResponse> getWordsByVocabularyGroupId(@RequestParam long vocabularyGroupId) {
    return ResponseEntity.ok(userVocabularyService.getWordWithVocGroup(vocabularyGroupId));
  }

  @PostMapping("/regenerate-examples")
  public ResponseEntity<MapSB> regenerateExamples(@RequestBody RegenerateExamplesRequest regenerateExamplesRequest) {
    userVocabularyService.regenerateExample(regenerateExamplesRequest);
    return ResponseEntity.ok(HttpUtils.successResponse());
  }

  @DeleteMapping("/delete-word/{id}")
  public ResponseEntity<MapSB> deleteWord(@PathVariable long id) {
    userVocabularyService.deleteWord(id);
    return ResponseEntity.ok(HttpUtils.successResponse());
  }

  @GetMapping("/getVocabularyStatistics")
  public ResponseEntity<List<LearningSessionRepository.RecentLearningSession>> getVocabularyStatistics(@RequestParam Long vocabularyId, @RequestParam int days) {
    return ResponseEntity.ok(vocabularyService.getRecentLearningSessions(vocabularyId, days));
  }
}
