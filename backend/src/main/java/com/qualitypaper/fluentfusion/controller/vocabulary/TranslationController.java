package com.qualitypaper.fluentfusion.controller.vocabulary;

import com.qualitypaper.fluentfusion.controller.dto.request.WordInformationRequest;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.PossibleTranslationsResponse;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.service.pts.translation.TranslationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/translation")
public class TranslationController {


  private final TranslationService translationService;

  public TranslationController(TranslationService translationService) {
    this.translationService = translationService;
  }

  @GetMapping("/autocomplete")
  public ResponseEntity<List<String>> autocomplete(@RequestParam String str, @RequestParam Language sourceLanguage, @RequestParam Language targetLanguage) {
    return ResponseEntity.ok(translationService.autocomplete(str, sourceLanguage, targetLanguage));
  }

  @PostMapping("/get-possible-translations")
  public ResponseEntity<PossibleTranslationsResponse> getPossibleTranslations(@RequestBody WordInformationRequest wordInformationRequest) {
    return ResponseEntity.ok(
            translationService.getPossibleTranslations(
                    wordInformationRequest.word(),
                    wordInformationRequest.sourceLanguage(),
                    wordInformationRequest.targetLanguage()
            )
    );
  }
}
