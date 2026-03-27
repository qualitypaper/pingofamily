package com.qualitypaper.fluentfusion.service.vocabulary.wordExample;

import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordExample;
import com.qualitypaper.fluentfusion.repository.WordExampleRepository;
import com.qualitypaper.fluentfusion.service.pts.tts.TextToSpeechService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WordExampleService {

  private final WordExampleRepository wordExampleRepository;
  private final TextToSpeechService ttsService;

  public static boolean checkValidity(WordExample wordExampleTo) {

    return wordExampleTo != null
            && wordExampleTo.getExample() != null;
  }

  @Transactional
  public WordExample createAndSaveWordExample(String example, Word word) {
    String key = ttsService.generateSoundFile(example, word.getLanguage());
    return createAndSaveWordExample(example, key, word);
  }

  public WordExample createAndSaveWordExample(String example, String soundUrl, Word word) {
    Optional<WordExample> alreadyCreated = alreadyCreated(example, word.getLanguage());
    if (alreadyCreated.isPresent()) return alreadyCreated.get();

    var wordExample = create(example, word, soundUrl);
    wordExampleRepository.save(wordExample);
    return wordExample;
  }

  public Optional<WordExample> alreadyCreated(String example, Language language) {
    return wordExampleRepository.findTopByExampleAndLanguage(example, language);
  }

  private WordExample create(String example, Word word, String soundUrl) {

    return WordExample.builder()
            .example(example)
            .language(word.getLanguage())
            .soundUrl(soundUrl)
            .createdAt(LocalDateTime.now())
            .word(word)
            .build();
  }

  public WordExample save(WordExample wordExampleFrom) {
    return wordExampleRepository.save(wordExampleFrom);
  }

  public void delete(WordExample wordExample) {
    wordExample.setWord(null);
    wordExampleRepository.delete(wordExample);
  }

  public Optional<WordExample> findById(Long wordExampleFromId) {
    return wordExampleRepository.findById(wordExampleFromId);
  }
}
