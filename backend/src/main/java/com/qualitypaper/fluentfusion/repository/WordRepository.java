package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

  Optional<Word> findTopByWordAndPosAndLanguage(String word, PartOfSpeech partOfSpeech, Language language);

  Optional<Word> findByWordAndLanguage(String word, Language language);

  List<Word> findAllByWordAndLanguage(String word, Language language);

  @Query(value = """
          select id, sound_url from word
          """, nativeQuery = true)
  List<Object[]> findAllSoundUrls();
}