package com.qualitypaper.fluentfusion.controller.admin;

import com.qualitypaper.fluentfusion.controller.dto.request.UpdateWordDictionaryRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.WordUpdateRequest;
import com.qualitypaper.fluentfusion.service.typealiase.MapSB;
import com.qualitypaper.fluentfusion.service.vocabulary.word.WordAdminService;
import com.qualitypaper.fluentfusion.service.vocabulary.word.WordDbService;
import com.qualitypaper.fluentfusion.service.vocabulary.word.WordService;
import com.qualitypaper.fluentfusion.util.HttpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/word")
public class WordAdminController {

    private final WordDbService wordDbService;
    private final WordService wordService;
    private final WordAdminService wordAdminService;

    @DeleteMapping("/delete")
    public MapSB deleteWord(@RequestParam Long id) {
        wordDbService.deleteWord(id);
        return HttpUtils.successResponse();
    }

    @PutMapping("/regenerate-conjugations/{id}")
    public MapSB regenerateConjugations(@PathVariable Long id) {
        wordService.regenerateConjugations(id);
        return HttpUtils.successResponse();
    }

    @PutMapping("/regenerate-sound/{id}")
    public MapSB regenerateSound(@PathVariable Long id) {
        wordService.regenerateSound(id);
        return HttpUtils.successResponse();
    }

    @PatchMapping("/update/{id}")
    public MapSB update(@PathVariable Long id, @RequestBody WordUpdateRequest request) {
        wordAdminService.update(id, request);
        return HttpUtils.successResponse();
    }

    @PatchMapping("/update-word-dictionary/{id}")
    public MapSB updateWordDictionary(@PathVariable Long id, @RequestBody UpdateWordDictionaryRequest request) {
        wordAdminService.updateWordDictionary(id, request);
        return HttpUtils.successResponse();
    }

}
