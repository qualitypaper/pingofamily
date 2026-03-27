package com.qualitypaper.fluentfusion.service.vocabulary.dictionary;

import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.WordDictionaryResponse;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.Conjugation;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.Desc;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.DescTranslation;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.WordDictionary;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import com.qualitypaper.fluentfusion.repository.DescRepository;
import com.qualitypaper.fluentfusion.repository.DescTranslationRepository;
import com.qualitypaper.fluentfusion.repository.WordDictionaryRepository;
import com.qualitypaper.fluentfusion.service.pts.dictionary.LookUpService;
import com.qualitypaper.fluentfusion.service.pts.translation.TranslationService;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.conjugation.ConjugationService;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json.DescJson;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json.WordDictionaryJson;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.TranslationProvider;
import com.qualitypaper.fluentfusion.service.vocabulary.word.UnneededWordsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class WordDictionaryService {

  private final DescRepository descRepository;
  private final DescTranslationRepository descTranslationRepository;
  private final WordDictionaryRepository wordDictionaryRepository;
  private final ConjugationService conjugationService;
  private final PosDescService posDescService;
  private final LookUpService lookUpService;
  private final TranslationService translationService;
  private final UnneededWordsService unneededWordsService;

  public static PartOfSpeech mapPos(String pos) {

    return pos != null ? switch (pos.toUpperCase()) {
      case "NOUN" -> PartOfSpeech.NOUN;
      case "VERB" -> PartOfSpeech.VERB;
      case "ADJECTIVE" -> PartOfSpeech.ADJECTIVE;
      default -> PartOfSpeech.OTHER;
    } : PartOfSpeech.OTHER;
  }

  public static WordDictionaryResponse formatWordDictionary(WordDictionary wordDictionary, Language targetLanguage) {
    return new WordDictionaryResponse(
            wordDictionary.getId(),
            wordDictionary.getDesc().getDescription(),
            getDescTranslationByLanguage(wordDictionary.getDesc().getDescriptionTranslations(), targetLanguage),
            wordDictionary.getSynonyms(),
            ConjugationService.formatConjugation(wordDictionary.getConjugation())
    );
  }

  public static String getDescTranslationByLanguage(List<DescTranslation> descTranslation, Language targetLanguage) {
    for (var e : descTranslation) {
      Language language = e.getLanguage();
      if (language.equals(targetLanguage)) {
        return e.getDescriptionTranslation();
      }
    }
    return null;
  }

  public static boolean checkValidity(WordDictionary wordDictionary) {
    return wordDictionary != null
            && wordDictionary.getConjugation() != null
            && wordDictionary.getDesc() != null
            && wordDictionary.getSynonyms() != null
            && ConjugationService.checkValidity(wordDictionary.getConjugation());
  }

  public void save(WordDictionary wordDictionary) {
    wordDictionaryRepository.save(wordDictionary);
  }

  // must be called within transaction
  @Transactional
  public WordDictionary addConjugationToWord(Word word) {
    String processWord = processWord(word);

    WordDictionaryJson wordDictionaryJson = lookUpService.lookUp(
            processWord,
            word.getLanguage(),
            word.getPos(),
            word.getWordType()).join();

    if (wordDictionaryJson == null) {
      return createAndSaveEmptyEntity(word);
    }
    var wordDictionary = create(word, wordDictionaryJson);
    wordDictionaryRepository.save(wordDictionary);

    return wordDictionary;
  }

  private String processWord(Word word) {
    return switch (word.getLanguage()) {
      case ENGLISH, GERMAN, SPANISH -> unneededWordsService.removeUnneededPart(word.getWord())[0];
      default -> word.getWord();
    };
  }

  private WordDictionary create(Word word, WordDictionaryJson wordDictionaryJson) {
    Desc desc = mapPosAndMeaning(new DescJson(wordDictionaryJson.getDesc()), List.of());
    CompletableFuture<Desc> descCompletableFuture = CompletableFuture.supplyAsync(() -> generateDescTranslations(desc.getDescription(), word, desc));

    Conjugation conjugation = conjugationService.createFrom(wordDictionaryJson.getConjugation(), word);
    if (conjugation == null) {
      log.info("Null Conjugation entity in create() in WordDictionaryService");
      return createAndSaveEmptyEntity(word);
    }

    Desc posDesc = descCompletableFuture.join();
    descRepository.save(posDesc);
    if (word.getPos().equals(PartOfSpeech.VERB)) {
      conjugationService.save(conjugation);
    }

    if (wordDictionaryRepository.existsByConjugation(conjugation)) {
      WordDictionary wordDictionary = wordDictionaryRepository.findByConjugation(conjugation).orElseThrow(
              () -> new RuntimeException("Couldn't find WordDictionary by Conjugation UNEXPECTED")
      );
      wordDictionary.setDesc(posDesc);
      wordDictionary.setSynonyms(wordDictionaryJson.getSynonyms());
      wordDictionary.setConjugation(conjugation);
      wordDictionary.setCreatedAt(LocalDateTime.now());

      return wordDictionary;
    }

    return WordDictionary.builder()
            .conjugation(conjugation)
            .synonyms(wordDictionaryJson.getSynonyms())
            .desc(desc)
            .createdAt(LocalDateTime.now())
            .build();
  }


  private Desc generateDescTranslations(String desc, Word word, Desc descToSave) {
    List<DescTranslation> descTranslationList = new ArrayList<>();

    for (Language language : Language.values()) {
      if (language.equals(word.getLanguage())) continue;

      String translation = translationService.translateText(
              desc,
              word.getLanguage(),
              language,
              TranslationProvider.AMAZON
      );
      if (translation == null) continue;

      DescTranslation descTranslation = DescTranslation.builder()
              .descriptionTranslation(translation)
              .language(language)
              .build();
      descTranslationList.add(descTranslation);
    }

    descTranslationRepository.saveAll(descTranslationList);

    descToSave.setDescriptionTranslations(descTranslationList);
    return descToSave;
  }

  private Desc mapPosAndMeaning(DescJson posDesc, List<DescTranslation> descTranslationList) {
    return posDescService.createAndSave(posDesc, descTranslationList);
  }

  public WordDictionary createAndSaveEmptyEntity(Word word) {
    var wordDictionary = WordDictionary.builder()
            .conjugation(conjugationService.createAndSaveEmptyEntity(word))
            .desc(posDescService.createEmpty())
            .synonyms(Collections.emptyList())
            .createdAt(LocalDateTime.now())
            .build();
    return wordDictionaryRepository.save(wordDictionary);
  }

  public void delete(WordDictionary wordDictionary) {
    if (wordDictionary == null) return;
    wordDictionaryRepository.delete(wordDictionary);
    log.info("Deleted WordDictionary with ID: {}", wordDictionary.getId());
  }

}
