package com.qualitypaper.fluentfusion.service.pts.dictionary;

import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordType;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json.WordDictionaryJson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class LookUpService {

  private final AIConjugationService aIConjugationService;
  private final ThesaurusService thesaurusService;
  private final DescriptionService descriptionService;


  public CompletableFuture<WordDictionaryJson> lookUp(String text, Language sourceLanguage, PartOfSpeech partOfSpeech, WordType wordType) {
    WordDictionaryJson wordDictionaryJson = new WordDictionaryJson();

    if (partOfSpeech.equals(PartOfSpeech.NOUN))
      wordDictionaryJson.setGender(getGender(text, sourceLanguage));

    CompletableFuture<String> desc = descriptionService.getDescription(text, sourceLanguage, partOfSpeech);
    CompletableFuture<Object> conjugation = aIConjugationService.makeConjugation(text, sourceLanguage, partOfSpeech, wordType);
    CompletableFuture<List<String>> synonyms = thesaurusService.getSynonyms(text, sourceLanguage);

    wordDictionaryJson.setDesc(desc.join());
    wordDictionaryJson.setConjugation(conjugation.join());
    wordDictionaryJson.setSynonyms(synonyms.join());

    return CompletableFuture.completedFuture(wordDictionaryJson);
  }


  private String getGender(String text, Language sourceLanguage) {
    String gender = "";
    if (Language.SPANISH.equals(sourceLanguage)) {
      if (text.endsWith("dad")
              || text.endsWith("tad")
              || text.endsWith("tud")
              || text.endsWith("umbre")
              || text.endsWith("dora")
              || text.endsWith("ción")
              || text.endsWith("sión")
              || text.endsWith("iz")
              || text.endsWith("a")) {
        gender = "Feminine";
      } else {
        gender = "Masculine";
      }
    }
    return gender;
  }

}
