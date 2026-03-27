package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.Conjugation;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConjugationRepository extends JpaRepository<Conjugation, Long> {

  boolean existsByWord(Word word);

  Optional<Conjugation> findByWord(Word word);
}
