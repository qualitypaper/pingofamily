package com.qualitypaper.fluentfusion.service.vocabulary.dictionary.conjugation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.ConjugationResponse;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.*;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.Conjugation;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import com.qualitypaper.fluentfusion.repository.ConjugationJsonEntityRepository;
import com.qualitypaper.fluentfusion.repository.ConjugationRepository;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json.conjugationJson.AdjectiveConjugationJson;
import com.qualitypaper.fluentfusion.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConjugationService {

  private static final List<String> RESPONSE_IGNORE_FIELDS = List.of("createdAt", "infinitive");

  private final ConjugationRepository conjugationRepository;
  private final VerbConjugationService verbConjugationService;
  private final NounConjugationService nounConjugationService;
  private final AdjectiveConjugationService adjectiveConjugationService;
  private final ConjugationJsonEntityRepository conjugationJsonEntityRepository;
  private final ObjectMapper objectMapper;

  public static ConjugationResponse formatConjugation(Conjugation conjugation) {
    if (conjugation == null || conjugation.getConjugationJsonEntity() == null) return null;
    ConjugationJsonEntity conjugationJson = conjugation.getConjugationJsonEntity();

    return switch (conjugationJson) {
      case NounConjugation nounConjugation -> new ConjugationResponse(
              conjugation.getConjugationJsonEntity().getId(),
              conjugationJson.getInfinitive(),
              Utils.resetFields(NounConjugationService.formatNounConjugation(nounConjugation), RESPONSE_IGNORE_FIELDS)
      );
      case VerbConjugation verbConjugation -> new ConjugationResponse(
              conjugation.getConjugationJsonEntity().getId(),
              conjugationJson.getInfinitive(),
              verbConjugation.getVerbTenseConjugation()
      );
      case AdjectiveConjugation adjectiveConjugation -> new ConjugationResponse(
              conjugation.getConjugationJsonEntity().getId(),
              conjugationJson.getInfinitive(),
              adjectiveConjugation.getMappings()
      );
      default -> null;
    };
  }

  public static boolean checkValidity(Conjugation conjugation) {
    return conjugation != null
            && conjugation.getConjugationJsonEntity() != null
            && conjugation.getConjugationJsonEntity().getPartOfSpeech() != null;
  }

  private Conjugation createAndSave(Word word, ConjugationJsonEntity conjugationJsonEntity) {
    Conjugation conjugation;

    if (conjugationRepository.existsByWord(word)) {
      Optional<Conjugation> existingConjugation = conjugationRepository.findByWord(word);
      if (existingConjugation.isEmpty())
        throw new IllegalStateException("Unexpected error");

      conjugation = existingConjugation.get();
      conjugation.setConjugationJsonEntity(conjugationJsonEntity);
      conjugation.setCreatedAt(LocalDateTime.now());
    } else {
      conjugation = Conjugation.builder()
              .conjugationJsonEntity(conjugationJsonEntity)
              .word(word)
              .createdAt(LocalDateTime.now())
              .build();
    }
    conjugationRepository.save(conjugation);

    if (conjugationJsonEntity instanceof VerbConjugation verbConjugation) {
      verbConjugation.setConjugation(conjugation);
      verbConjugationService.save(verbConjugation);
      conjugationRepository.save(conjugation);
    }
    return conjugation;
  }

  public void save(Conjugation conjugation) {
    conjugationRepository.save(conjugation);
  }

  public Conjugation createFrom(Object conjugation, Word word) {
    if (conjugation == null) return null;

    ConjugationJsonEntity conjugationJson = mapPos(conjugation, word);
//        conjugationJson.setInfinitive(word.getWord());
    if (conjugationJson == null) return createAndSaveEmptyEntity(word);

    conjugationJsonEntityRepository.save(conjugationJson);
    return createAndSave(word, conjugationJson);
  }

  @SuppressWarnings("unchecked")
  public ConjugationJsonEntity mapPos(Object value, Word word) {
    if (word.getPos() == null)
      throw new IllegalArgumentException();

    PartOfSpeech pos = word.getPos();
    Language language = word.getLanguage();
    String infinitive = word.getWord();

    Map<String, Object> map = null;

    if (pos.equals(PartOfSpeech.VERB)) {
      if (value instanceof String) {
        map = objectMapper.convertValue(value, Map.class);
      } else {
        map = (Map<String, Object>) value;
      }
    }

    return switch (pos) {
      case VERB -> verbConjugationService.createAndSave(getConjugationMapping(map), infinitive);
      case NOUN -> nounConjugationService.createAndSave(value, language);
      case ADJECTIVE -> {
        try {
          var mappedValue = objectMapper.convertValue(value, AdjectiveConjugationJson.class);
          yield adjectiveConjugationService.create(mappedValue.getInfinitive(), mappedValue.getMappings());
        } catch (IllegalArgumentException e) {
          log.error(e.getMessage(), e);
          log.error("Dropping conjugation generation for an adjective");
          yield adjectiveConjugationService.create(infinitive, new HashMap<>());
        }
      }
      default -> {
        var mappedValue = objectMapper.convertValue(value, ConjugationJsonEntity.class);
        yield new ConjugationJsonEntity(mappedValue.getInfinitive(), PartOfSpeech.OTHER);
      }
    };
  }

  private Object getConjugationMapping(Map<String, Object> map) {
    Object mapping = map.get("conjugations");
    return mapping == null ? map : mapping;
  }

  public Conjugation createAndSaveEmptyEntity(Word word) {
    ConjugationJsonEntity conjugationJsonEntity = conjugationJsonEntityRepository.save(
            new ConjugationJsonEntity()
    );

    var conjugation = Conjugation.builder()
            .conjugationJsonEntity(conjugationJsonEntity)
            .createdAt(LocalDateTime.now())
            .word(word)
            .build();
    conjugationRepository.save(conjugation);
    return conjugation;
  }
}
