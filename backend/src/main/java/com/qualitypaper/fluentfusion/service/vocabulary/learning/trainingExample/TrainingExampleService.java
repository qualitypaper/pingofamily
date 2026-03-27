package com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample;

import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.TrainingExampleListResponse;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.Difficulty;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.VerbConjugation;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.VerbTenseConjugation;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.Conjugation;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.Training;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TrainingType;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExample;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleData;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleKeys;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordType;
import com.qualitypaper.fluentfusion.repository.TrainingExampleRepository;
import com.qualitypaper.fluentfusion.service.db.queries.resultTypes.GetWordsForTraining;
import com.qualitypaper.fluentfusion.service.pts.examples.ExamplesGenerationService;
import com.qualitypaper.fluentfusion.service.pts.examples.ExamplesResponse;
import com.qualitypaper.fluentfusion.service.pts.tts.TextToSpeechService;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.TrainingExampleDataService;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.ExampleTranslationStruct;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.TranslationDirection;
import com.qualitypaper.fluentfusion.service.vocabulary.word.UnneededWordsService;
import com.qualitypaper.fluentfusion.util.types.Pair;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingExampleService {

  private static final String REPLACEMENT_STRING = " ### ";
  private final TrainingExampleRepository trainingExampleRepository;
  private final UnneededWordsService unneededWordsService;
  private final EntityManager entityManager;
  private final TrainingExampleDataService trainingExampleDataService;
  private final TrainingExampleStatisticsService trainingExampleStatisticsService;
  private final ExamplesGenerationService examplesGenerationService;
  private final TextToSpeechService textToSpeechService;


  @Value("${url.static.audio}")
  private String staticAudioUrl;


//    @Transactional
//    @EventListener(ApplicationReadyEvent.class)
//    void init() {
//        List<TrainingExample> trainingExamples = trainingExampleRepository.findAllByTrainingExampleStatistics(null);
//        if (trainingExamples.isEmpty()) {
//            return;
//        }
//
//        for (TrainingExample trainingExample : trainingExamples) {
//            trainingExample.setTrainingExampleStatistics(trainingExampleStatisticsService.create(false, false, trainingExample));
//        }
//    }

  public record Example(
          String example,
          String soundUrl,
          Language language
  ) {
  }


  public Pair<Example, Example> generateTrainingExample(WordTranslation wordTranslation, TrainingType trainingType) {

    Language learningLanguage = wordTranslation.getWordFrom().getLanguage();
    Language nativeLanguage = wordTranslation.getWordTo().getLanguage();
    boolean isReversed = TrainingExampleService.getTranslationDirection(trainingType).equals(TranslationDirection.REVERSED);

    ExampleTranslationStruct exampleTranslationStruct = ExampleTranslationStruct.builder()
            .translation(wordTranslation.getWordTo().getWord())
            .word(wordTranslation.getWordFrom().getWord())
            .sourceLanguage(learningLanguage)
            .targetLanguage(nativeLanguage)
            .difficulty(Difficulty.EASY)
            .partOfSpeech(wordTranslation.getWordFrom().getPos())
            .build();

    List<ExamplesResponse> response = examplesGenerationService.generateTrainingExamples(exampleTranslationStruct, trainingType);

    int fromIndex = 0;
    int toIndex = 1;
    for (int i = 0; i < response.size(); i++) {
      if (isReversed && response.get(i).language().equals(nativeLanguage)) {
        fromIndex = i;
      } else if (!isReversed && response.get(i).language().equals(learningLanguage)) {
        fromIndex = i;
      } else if (isReversed && response.get(i).language().equals(learningLanguage)) {
        toIndex = i;
      } else if (!isReversed && response.get(i).language().equals(nativeLanguage)) {
        toIndex = i;
      }
    }

    String soundUrlFrom = textToSpeechService.generateSoundFile(
            response.get(fromIndex).example(),
            response.get(fromIndex).language()
    );
    String soundUrlTo = textToSpeechService.generateSoundFile(
            response.get(toIndex).example(),
            response.get(toIndex).language()
    );

    return new Pair<>(
            new Example(response.get(fromIndex).example(), soundUrlFrom, response.get(fromIndex).language()),
            new Example(response.get(toIndex).example(), soundUrlTo, response.get(toIndex).language())
    );
  }

  public TrainingExampleListResponse formatTrainingExample(TrainingExample trainingExample) {
    if (trainingExample == null || trainingExample.getTrainingExampleData() == null) return null;
    TrainingExampleData trainingExampleData = trainingExample.getTrainingExampleData();


    return new TrainingExampleListResponse(
            trainingExample.getId(),
            trainingExampleData.getSentence(),
            trainingExampleData.getSentenceTranslation(),
            trainingExampleData.getFormattedString(),
            staticAudioUrl + trainingExampleData.getSoundUrl(),
            mapWordsTranslation(trainingExampleData.getWordsTranslation()),
            trainingExampleData.getTrainingType(),
            trainingExampleData.getIdentifiedWord()
    );
  }

  public static Map<String, List<String>> mapWordsTranslation(List<TrainingExampleKeys> list) {
    Map<String, List<String>> map = new HashMap<>();
    for (TrainingExampleKeys trainingExampleKeys : list) {
      if (trainingExampleKeys.getValues() == null) {
        map.put(trainingExampleKeys.getKey(), new ArrayList<>());
        continue;
      }
      List<String> translations = trainingExampleKeys.getValues()
              .stream()
              .flatMap(e -> Stream.of(e.getTranslation()))
              .toList();
      map.put(trainingExampleKeys.getKey(), translations);
    }
    return map;
  }

  private static String[] findFormattingPartWithLevDistance(String sentence, String word) {
    String[] split = sentence.split(" ");
    char[] wordChars = word.toCharArray();
    int currMin = Integer.MAX_VALUE;
    int index = 0;

    for (int i = 0; i < split.length; i++) {
      int current = levensteinDistance(wordChars, split[i].toCharArray());
      if (current < currMin) {
        index = i;
        currMin = current;
      }
    }

    String identifiedString = split[index];
    split[index] = REPLACEMENT_STRING;
    return new String[]{String.join(" ", split), identifiedString};
  }

  public static int levensteinDistance(char[] s1, char[] s2) {

    int[] prev = new int[s2.length + 1];

    for (int j = 0; j < s2.length + 1; j++) {
      prev[j] = j;
    }

    for (int i = 1; i < s1.length + 1; i++) {
      int[] curr = new int[s2.length + 1];
      curr[0] = i;

      for (int j = 1; j < s2.length + 1; j++) {
        int d1 = prev[j] + 1;
        int d2 = curr[j - 1] + 1;
        int d3 = prev[j - 1];
        if (s1[i - 1] != s2[j - 1]) {
          d3 += 1;
        }
        curr[j] = min(d1, d2, d3);
      }

      prev = curr;
    }
    return prev[s2.length];
  }

  private static int min(int... numbers) {
    return IntStream.of(numbers).min().orElse(Integer.MAX_VALUE);
  }

  public static TranslationDirection getTranslationDirection(TrainingType trainingType) {
    return trainingType.name().contains("REVERSED") ? TranslationDirection.REVERSED : TranslationDirection.INITIAL;
  }

  public static boolean isReversedTraining(TrainingType trainingType) {
    return getTranslationDirection(trainingType).equals(TranslationDirection.REVERSED);
  }

  public static boolean isHardTraining(TrainingType trainingType) {
    return trainingType.equals(TrainingType.SENTENCE_TYPE) || trainingType.equals(TrainingType.SENTENCE_AUDIO);
  }

  public static boolean isSentenceRequired(TrainingType trainingType) {
    return switch (trainingType) {
      case COMPLETE_EMPTY_SPACES, PHRASE_CONSTRUCTION, PHRASE_CONSTRUCTION_REVERSED,
           SENTENCE_AUDIO, SENTENCE_TYPE -> true;
      case TRANSLATION, AUDIO -> false;
    };
  }

  @Transactional
  public TrainingExample createTrainingExampleAndSave(WordTranslation wordTranslation,
                                                      Training training,
                                                      String sentence,
                                                      String translation,
                                                      String soundUrl,
                                                      TranslationDirection translationDirection,
                                                      TrainingType trainingType,
                                                      Optional<Conjugation> conjugation
  ) {
    log.info("Creating training example for user vocabulary, trainingType: {}", trainingType);
    Word wordFrom = wordTranslation.getWordFrom();

    String[] formattedSentence = trainingType.equals(TrainingType.COMPLETE_EMPTY_SPACES)
            ? formatTrainingSentence(sentence, Optional.of(wordFrom), conjugation)
            : new String[]{sentence, wordFrom.getWord()};

    return saveTrainingExample(new TrainingExampleDataService.TrainingExampleParams(
            wordTranslation,
            training,
            sentence,
            translation,
            soundUrl,
            translationDirection,
            trainingType,
            formattedSentence
    ));
  }

  private TrainingExample saveTrainingExample(TrainingExampleDataService.TrainingExampleParams trainingExampleParams) {
    TrainingExampleData trainingExampleData = trainingExampleDataService.createAndSave(trainingExampleParams);

    TrainingExample trainingExample = TrainingExample.builder()
            .trainingExampleData(trainingExampleData)
            .training(trainingExampleParams.training())
            .createdAt(LocalDateTime.now())
            .build();

    return trainingExampleRepository.save(trainingExample);
  }

  public void save(TrainingExample trainingExample) {
    trainingExampleRepository.save(trainingExample);
  }

  public List<TrainingExampleListResponse> formatTrainingExampleForTraining(List<TrainingExample> list) {
    return list.stream().map(this::formatTrainingExample).toList();
  }

  public String[] formatTrainingSentence(String sentence, Optional<Word> wordFrom, Optional<Conjugation> conjugation) {
    if (wordFrom.isEmpty()) return new String[]{sentence, ""};

    Word word = wordFrom.get();

    return formatTrainingSentence(sentence, word.getWord(), word.getPos(),
            word.getWordType(), word.getLanguage(),
            conjugation);
  }

  // return array -> first index is the formatted sentence and the second one is the identified word (filtered)
  public String[] formatTrainingSentence(String sentence, String word, PartOfSpeech pos, WordType wordType, Language language, Optional<Conjugation> conjugation) {
    int startIndex = sentence.indexOf(word);

    if (sentence.contains(REPLACEMENT_STRING) && startIndex != -1) {
      int endIndex = startIndex + word.length();
      while (endIndex < sentence.length() && !Character.isSpaceChar(sentence.charAt(endIndex))) endIndex++;

      return new String[]{sentence, sentence.substring(startIndex, endIndex + 1)};
    } else if (startIndex != -1) {
      int endIndex = startIndex + word.length() - 1;
      while (endIndex < sentence.length() && !Character.isSpaceChar(sentence.charAt(endIndex))) endIndex++;
      endIndex++;
      while (endIndex >= sentence.length()) endIndex--;

      return new String[]{sentence.replaceFirst(word, REPLACEMENT_STRING), sentence.substring(startIndex, endIndex)};
    } else if (Objects.requireNonNull(pos).equals(PartOfSpeech.VERB)) {
      if (wordType.equals(WordType.VERB_WITH_PREPOSITION) && language.equals(Language.GERMAN)) {
        String[] filtered = unneededWordsService.removeUnneededPart(word, Language.GERMAN);
        String withInfiniteIdentifier = filtered[1] + "zu" + filtered[0];

        int indexOfFiltered = sentence.indexOf(filtered[0]);

        if (sentence.contains(withInfiniteIdentifier))
          return new String[]{sentence.replaceFirst(withInfiniteIdentifier, REPLACEMENT_STRING), withInfiniteIdentifier};
        else if (indexOfFiltered == 0 || (indexOfFiltered != -1 && Character.isSpaceChar(sentence.charAt(indexOfFiltered - 1))))
          return new String[]{sentence.replaceFirst(filtered[0], REPLACEMENT_STRING), filtered[0]};
      }

      VerbConjugation verbConjugation = castToVerbConjugation(conjugation);

      if (verbConjugation == null) return findFormattingPartWithLevDistance(sentence, word);

      return findFormattingPartForVerbs(sentence, verbConjugation, word, wordType, language);
    } else {
      return findFormattingPartWithLevDistance(sentence, word);
    }
  }

  private VerbConjugation castToVerbConjugation(Optional<Conjugation> conjugation) {
    if (conjugation.isEmpty() || conjugation.get().getConjugationJsonEntity() == null)
      return null;

    try {
      return (VerbConjugation) conjugation.get().getConjugationJsonEntity();
    } catch (ClassCastException e) {
      log.error(e.getMessage(), e);
      return null;
    }

  }

  private String[] findFormattingPartForVerbs(String sentence, VerbConjugation conjugation, String word,
                                              WordType wordType, Language language) {
    boolean partRemoved = !wordType.equals(WordType.PHRASE) && !wordType.equals(WordType.WORD) && !wordType.equals(WordType.SENTENCE);

    for (VerbTenseConjugation verbTenseConjugation : conjugation.getVerbTenseConjugation()) {
      for (String value : verbTenseConjugation.getTenseConjugations().values()) {
        if (partRemoved) {
          value = unneededWordsService.removeUnneededPart(value, language)[0];
          value = unneededWordsService.removeTenseIdentifier(value);
        }

        if (sentence.contains(value)) {
          return new String[]{sentence.replaceFirst(value, REPLACEMENT_STRING), value};
        }
      }
    }

    return findFormattingPartWithLevDistance(sentence, word);
  }

  public Map<Long, Map<String, List<String>>> getWordsTranslationForTrainingExamples(List<GetWordsForTraining> list) {
    Query query = entityManager.createNativeQuery("""
            SELECT tek.key, te.id as training_example_id,
                           array_agg(DISTINCT tje.translation) AS translations
            FROM training_example te
            JOIN training_example_keys tek ON tek.training_example_keys_id = te.id
            LEFT JOIN translation_json_entity tje ON tje.translation_json_id = tek.id
                where te.id in (:idsList)
            GROUP BY tek.key, te.id
            """).setParameter("idsList", list.stream().map(e -> e.trainingExampleId).toList());
    @SuppressWarnings("unchecked")
    List<Object[]> resultList = query.getResultList();

    return processWordsTranslationsFromNativeQuery(resultList);
  }


  private Map<Long, Map<String, List<String>>> processWordsTranslationsFromNativeQuery(List<Object[]> list) {
    Map<Long, Map<String, List<String>>> map = new HashMap<>();

    for (Object[] objects : list) {
      Long trainingExampleId = (Long) objects[1];
      map.computeIfAbsent(trainingExampleId, k -> new HashMap<>());
      map.get(trainingExampleId).put(String.valueOf(objects[0]), Arrays.asList((String[]) objects[2]));
    }

    return map;
  }

  @Transactional
  public TrainingExample copy(TrainingExample trainingExample, Training newTraining) {
    TrainingExample te = TrainingExample.builder()
            .createdAt(LocalDateTime.now())
            .trainingExampleData(trainingExample.getTrainingExampleData())
            .training(newTraining)
            .build();

    return trainingExampleRepository.save(te);
  }

}
