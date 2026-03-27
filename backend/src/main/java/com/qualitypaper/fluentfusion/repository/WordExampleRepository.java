package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordExample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordExampleRepository extends JpaRepository<WordExample, Long> {
  Optional<WordExample> findTopByExampleAndLanguage(String example, Language language);

  List<WordExample> findAllByWord(Word word);
}
