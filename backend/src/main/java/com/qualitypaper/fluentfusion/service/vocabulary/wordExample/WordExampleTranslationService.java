package com.qualitypaper.fluentfusion.service.vocabulary.wordExample;

import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordExample;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordExampleTranslation;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import com.qualitypaper.fluentfusion.repository.WordExampleTranslationRepository;
import com.qualitypaper.fluentfusion.service.pts.examples.ExamplesGenerationService;
import com.qualitypaper.fluentfusion.service.pts.examples.ExamplesResponse;
import com.qualitypaper.fluentfusion.service.pts.tts.TextToSpeechService;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.ExampleTranslationStruct;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.UserVocabularyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class WordExampleTranslationService {

  private final WordExampleTranslationRepository wordExampleTranslationRepository;
  private final WordExampleService wordExampleService;
  private final TextToSpeechService textToSpeechService;
  private final ExamplesGenerationService examplesGenerationService;

  @Async
  @Transactional
  public CompletableFuture<WordExampleTranslation> createWordExampleTranslation(WordTranslation wordTranslation, Vocabulary vocabulary) {
    ExampleTranslationStruct exampleTranslationStruct = ExampleTranslationStruct.builder()
            .word(wordTranslation.getWordFrom().getWord())
            .translation(wordTranslation.getWordTo().getWord())
            .sourceLanguage(vocabulary.getLearningLanguage())
            .targetLanguage(vocabulary.getNativeLanguage())
            .difficulty(vocabulary.getCreatedBy().getUserLevel())
            .partOfSpeech(wordTranslation.getWordTo().getPos())
            .build();

    if (countAllExamplesOfWord(wordTranslation) >= 50) {
      return CompletableFuture.completedFuture(getRandomWordExample(wordTranslation));
    } else {
      List<ExamplesResponse> result = examplesGenerationService.generateExamples(exampleTranslationStruct);
      log.info("Generated examples for {}, those are -> {}", vocabulary, result);
      return CompletableFuture.completedFuture(createWordExampleTranslation(result, wordTranslation, vocabulary));
    }
  }

  private WordExampleTranslation createWordExampleTranslation(List<ExamplesResponse> examples, WordTranslation wordTranslation, Vocabulary vocabulary) {
    String[] wordExamples = UserVocabularyService.getExamplesInOrder(vocabulary.getLearningLanguage(), vocabulary.getNativeLanguage(), examples);

    var wordExampleFrom = wordExampleService.createAndSaveWordExample(wordExamples[0], wordTranslation.getWordFrom());
    var wordExampleTo = wordExampleService.createAndSaveWordExample(wordExamples[1], wordTranslation.getWordTo());

    return createAndSaveWordExampleTranslation(wordTranslation, wordExampleFrom, wordExampleTo);
  }


  @Transactional
  public WordExampleTranslation generateSound(WordExampleTranslation wordExampleTranslation) {
    String soundFrom = textToSpeechService.generateSoundFile(wordExampleTranslation.getWordExampleFrom().getExample(), wordExampleTranslation.getWordExampleFrom().getLanguage());
    String soundTo = textToSpeechService.generateSoundFile(wordExampleTranslation.getWordExampleTo().getExample(), wordExampleTranslation.getWordExampleTo().getLanguage());

    wordExampleTranslation.getWordExampleFrom().setSoundUrl(soundFrom);
    wordExampleTranslation.getWordExampleTo().setSoundUrl(soundTo);

    wordExampleService.save(wordExampleTranslation.getWordExampleFrom());
    wordExampleService.save(wordExampleTranslation.getWordExampleTo());

    wordExampleTranslationRepository.save(wordExampleTranslation);
    return wordExampleTranslation;
  }

  public WordExampleTranslation createAndSaveWordExampleTranslation(WordTranslation wordTranslation, WordExample wordExampleFrom, WordExample wordExampleTo) {
    var wordExampleTranslation = create(wordTranslation, wordExampleFrom, wordExampleTo);
    wordExampleTranslationRepository.save(wordExampleTranslation);
    return wordExampleTranslation;
  }

  private WordExampleTranslation create(WordTranslation wordTranslation, WordExample wordExample1, WordExample wordExample2) {
    return WordExampleTranslation.builder()
            .wordTranslation(wordTranslation)
            .wordExampleFrom(wordExample1)
            .wordExampleTo(wordExample2)
            .createdAt(LocalDateTime.now())
            .build();
  }

  public WordExampleTranslation createEmpty(WordExample wordExample1, WordExample wordExample2) {
    var wordExampleTranslation = WordExampleTranslation.builder()
            .wordExampleFrom(wordExample1)
            .wordExampleTo(wordExample2)
            .createdAt(LocalDateTime.now())
            .build();
    wordExampleTranslationRepository.save(wordExampleTranslation);
    return wordExampleTranslation;
  }

  public int countAllExamplesOfWord(WordTranslation wordTranslation) {
    return wordExampleTranslationRepository.countAllByWordTranslation(wordTranslation);
  }

  public WordExampleTranslation getRandomWordExample(WordTranslation wordTranslation) {
    List<WordExampleTranslation> allByWordTranslation = wordExampleTranslationRepository
            .getAllByWordTranslation(wordTranslation);

    int index = new Random().nextInt(allByWordTranslation.size());
    return allByWordTranslation.get(index);

  }

  public void delete(WordExampleTranslation wordExampleTranslation) {
    wordExampleTranslation.setWordTranslation(null);
    wordExampleTranslation.setWordExampleTo(null);
    wordExampleTranslation.setWordExampleFrom(null);
    wordExampleTranslationRepository.delete(wordExampleTranslation);
  }
}