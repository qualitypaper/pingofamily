package com.qualitypaper.fluentfusion.service.vocabulary.learning;

import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.PossibleTranslationsResponse;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TranslationJsonEntity;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleKeys;
import com.qualitypaper.fluentfusion.repository.TrainingExampleKeysRepository;
import com.qualitypaper.fluentfusion.repository.TranslationJsonEntityRepository;
import com.qualitypaper.fluentfusion.service.pts.translation.TranslationService;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.WordDictionaryService;
import com.qualitypaper.fluentfusion.service.vocabulary.word.TranslationJson;
import com.qualitypaper.fluentfusion.service.vocabulary.word.UnneededWordsService;
import com.qualitypaper.fluentfusion.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingExampleKeysService {

  private final TranslationService translationService;
  private final UnneededWordsService unneededWordsService;
  private final TranslationJsonEntityRepository translationJsonEntityRepository;
  private final TrainingExampleKeysRepository trainingExampleKeysRepository;

  @Transactional
  public List<TrainingExampleKeys> createWordsTranslation(String sentence, Language learningLanguage, Language nativeLanguage) {
    sentence = StringUtils.removeSpecialCharacters(sentence);
    List<TrainingExampleKeys> list = new ArrayList<>();

    String[] split = sentence.split(" ");
    List<String> strings = formatSplitArray(split);

    for (String word : strings) {
      PossibleTranslationsResponse translations = translationService.getPossibleTranslations(word, learningLanguage, nativeLanguage);

      if (translations != null && !translations.possibleTranslations().isEmpty()) {
        addToList(word, translations.possibleTranslations(), list);
      }
    }
    trainingExampleKeysRepository.saveAll(list);

    return list;
  }

  private void addToList(String wordFrom, List<TranslationJson> wordTo, List<TrainingExampleKeys> list) {
    List<TranslationJsonEntity> translationJsonEntities = new ArrayList<>();
    for (TranslationJson translationJson : wordTo) {
      TranslationJsonEntity translationJsonEntity = TranslationJsonEntity.builder()
              .translation(translationJson.getTranslation())
              .partOfSpeech(WordDictionaryService.mapPos(translationJson.getPos()))
              .build();

      translationJsonEntities.add(translationJsonEntity);
    }
    translationJsonEntityRepository.saveAll(translationJsonEntities);

    TrainingExampleKeys trainingExampleKeys = TrainingExampleKeys.builder()
            .key(wordFrom)
            .values(translationJsonEntities)
            .build();

    trainingExampleKeysRepository.save(trainingExampleKeys);
    list.add(trainingExampleKeys);
  }


  private List<String> formatSplitArray(String[] split) {
    List<String> list = new ArrayList<>(Arrays.stream(split).toList());
    list.removeIf(e -> e.isEmpty() || unneededWordsService.isUnneededWord(e));

    return list;
  }

}
