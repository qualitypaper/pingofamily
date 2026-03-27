package com.qualitypaper.fluentfusion.mappers.word;

import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.WordListResponse;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.WordResponse;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.WordDictionaryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WordMapperImplementation implements WordMapper {

  @Value("${url.static.audio}")
  private String staticAudioUrl;

  @Override
  public WordResponse mapFrom(Word word, boolean hideWordDictionary, Language targetLanguage) {
    if (word == null) {
      return null;
    }

    return new WordResponse(
            word.getId(),
            word.getWord(),
            word.getLanguage(),
            word.getImageUrl(),
            staticAudioUrl + word.getSoundUrl(),
            word.getPos(),
            !hideWordDictionary && word.getWordDictionary() != null
                    ? WordDictionaryService.formatWordDictionary(word.getWordDictionary(), targetLanguage)
                    : null
    );
  }

  @Override
  public WordListResponse mapFrom(Object[] word) {
    return WordListResponse.builder()
            .userVocabularyId((long) word[0])
            .wordFrom((String) word[1])
            .soundUrl(staticAudioUrl + word[2])
            .partOfSpeech((String) word[3])
            .wordTo((String) word[4])
            .build();
  }
}
