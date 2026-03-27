package com.qualitypaper.fluentfusion.service.vocabulary.dictionary.conjugation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.GermanNounConjugationResponse;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.NounConjugation;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.NounMappings;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.languageNounConjugations.*;
import com.qualitypaper.fluentfusion.repository.InnerMapRepository;
import com.qualitypaper.fluentfusion.repository.KeyValueRepository;
import com.qualitypaper.fluentfusion.repository.NounConjugationRepository;
import com.qualitypaper.fluentfusion.repository.NounMappingsRepository;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json.conjugationJson.nounLanguageConjugations.EnglishNounConjugationJson;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json.conjugationJson.nounLanguageConjugations.GermanNounConjugationJson;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json.conjugationJson.nounLanguageConjugations.SpanishNounConjugationJson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NounConjugationService {

  private final NounConjugationRepository nounConjugationRepository;
  private final NounMappingsRepository nounMappingsRepository;
  private final InnerMapRepository innerMapRepository;
  private final KeyValueRepository keyValueRepository;
  private final ObjectMapper objectMapper;


  @Transactional
  public NounConjugation createAndSave(Object value, Language language) {


    return switch (language) {
      case ENGLISH -> {
        try {
          var mappedValue = objectMapper.convertValue(value, EnglishNounConjugationJson.class);
          yield createAndSave(mappedValue);
        } catch (IllegalArgumentException e) {
          log.info(e.getMessage());
          yield createAndSave(new EnglishNounConjugationJson());
        }
      }
      case GERMAN -> {
        try {
          var germanNounConjugation = objectMapper.convertValue(value, GermanNounConjugationJson.class);
          yield createAndSave(germanNounConjugation);
        } catch (IllegalArgumentException e) {
          log.info(e.getMessage());
          yield createAndSave(new GermanNounConjugationJson());
        }
      }
      case SPANISH -> {
        try {
          var spanishNounConjugation = objectMapper.convertValue(value, SpanishNounConjugationJson.class);
          yield createAndSave(spanishNounConjugation);
        } catch (IllegalArgumentException e) {
          log.info(e.getMessage());
          yield createAndSave(new SpanishNounConjugationJson());
        }
      }
      case ROMANIAN -> null;
      default -> throw new IllegalStateException("Unexpected value: " + language);
    };
  }


  public static Object formatNounConjugation(NounConjugation conjugationJson) {
    if (conjugationJson == null || conjugationJson.getMapping() == null) return null;

    NounMappings mapping = conjugationJson.getMapping();
    if (mapping instanceof EnglishNounConjugation || mapping instanceof SpanishNounConjugation) {
      return conjugationJson;
    } else if (mapping instanceof GermanNounConjugation germanMapping) {
      Map<String, Map<String, String>> resultMap = new HashMap<>();
      for (InnerMap element : germanMapping.getMappings()) {
        resultMap.put(
                element.getMapKey(),
                element.getValueList()
                        .stream()
                        .collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue))
        );
      }
      return new GermanNounConjugationResponse(
              germanMapping.getGender(),
              resultMap
      );
    }
    return null;
  }

  public NounConjugation createAndSave(String plural, NounMappings mappings) {
    return NounConjugation.builder()
            .mapping(mappings)
            .plural(plural)
            .build();
  }

  public NounConjugation createAndSave(GermanNounConjugationJson germanNounConjugationJson) {
    var nounConjugation = createAndSave(germanNounConjugationJson.getPlural(), map(germanNounConjugationJson));
    return createAndSave(nounConjugation);
  }

  private NounMappings map(GermanNounConjugationJson germanNounConjugationJson) {
    return GermanNounConjugation.builder()
            .gender(germanNounConjugationJson.getGender())
            .mappings(createMappings(germanNounConjugationJson.getMappings()))
            .build();
  }

  private List<InnerMap> createMappings(Map<String, Map<String, String>> mappings) {
    List<InnerMap> list = new ArrayList<>();

    if (mappings == null) return Collections.emptyList();

    for (Map.Entry<String, Map<String, String>> entry : mappings.entrySet()) {
      list.add(createAndSave(entry.getKey(), entry.getValue()));
    }

    return list;
  }

  public InnerMap createAndSave(String key, Map<String, String> value) {
    var innerMap = new InnerMap();
    innerMap.setMapKey(key);
    List<KeyValue> list = new ArrayList<>();
    for (Map.Entry<String, String> entry : value.entrySet()) {
      var keyValue = new KeyValue(null, entry.getKey(), entry.getValue());
      keyValueRepository.save(keyValue);
      list.add(keyValue);
    }
    innerMap.setValueList(list);
    innerMap.setCreatedAt(LocalDateTime.now());
    innerMapRepository.save(innerMap);
    return innerMap;
  }

  public NounConjugation createAndSave(NounConjugation nounConjugation) {
    nounConjugation.setPartOfSpeech(PartOfSpeech.NOUN);
    nounMappingsRepository.save(nounConjugation.getMapping());
    nounConjugationRepository.save(nounConjugation);
    return nounConjugation;
  }

  public NounConjugation createAndSave(EnglishNounConjugationJson englishNounConjugationJson) {
    var nounConjugation = createAndSave(englishNounConjugationJson.getPlural(), map(englishNounConjugationJson));
    return createAndSave(nounConjugation);
  }

  private NounMappings map(EnglishNounConjugationJson englishNounConjugationJson) {
    return new EnglishNounConjugation(englishNounConjugationJson.getPossessive(), englishNounConjugationJson.getPluralPossessive());
  }

  public void save(NounMappings nounMappings) {
    nounMappingsRepository.save(nounMappings);
  }

  public NounConjugation createAndSave(SpanishNounConjugationJson spanishNounConjugation) {
    var nounConjugation = createAndSave(spanishNounConjugation.getPlural(), map(spanishNounConjugation));
    return createAndSave(nounConjugation);
  }

  private NounMappings map(SpanishNounConjugationJson spanishNounConjugation) {
    return SpanishNounConjugation.builder()
            .pluralFeminine(spanishNounConjugation.getPluralFeminine())
            .singularFeminine(spanishNounConjugation.getSingularFeminine())
            .build();
  }
}
