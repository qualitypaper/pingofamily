package com.qualitypaper.fluentfusion.service.vocabulary.word;

import com.qualitypaper.fluentfusion.controller.dto.request.UpdateWordDictionaryRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.WordUpdateRequest;
import com.qualitypaper.fluentfusion.exception.notfound.NotFoundException;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.WordDictionary;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import com.qualitypaper.fluentfusion.repository.WordRepository;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.WordDictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WordAdminService {

    private final WordRepository wordRepository;
    private final WordDictionaryService wordDictionaryService;

    @Async
    @Transactional
    public void update(long id, WordUpdateRequest request) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Word with id: " + id + " wasn't found"));

        if (request.word() != null && !request.word().equals(word.getWord())) {
            word.setWord(request.word());
        }

        if (request.partOfSpeech() != null && !request.partOfSpeech().equals(word.getPos())) {
            word.setPos(request.partOfSpeech());
        }

        if (request.soundUrl() != null && !request.soundUrl().equals(word.getSoundUrl())) {
            word.setSoundUrl(request.soundUrl());
        }

        if (request.imageUrl() != null && !request.imageUrl().equals(word.getImageUrl())) {
            word.setImageUrl(request.imageUrl());
        }

        wordRepository.save(word);
    }

    @Async
    @Transactional
    public void updateWordDictionary(long wordId, UpdateWordDictionaryRequest request) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new NotFoundException("Word with id: " + wordId + " wasn't found"));
        WordDictionary wordDictionary = word.getWordDictionary();
        if (wordDictionary == null) {
            wordDictionary = wordDictionaryService.createAndSaveEmptyEntity(word);
        }

        if (request.desc() != null && !request.desc().equals(wordDictionary.getDesc().getDescription())) {
            wordDictionary.getDesc().setDescription(request.desc());
        }

        if (request.synonyms() != null && !request.synonyms().isEmpty()) {
           wordDictionary.setSynonyms(request.synonyms());
        }

        wordDictionaryService.save(wordDictionary);
    }
}
