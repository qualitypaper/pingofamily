package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.Conjugation;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.WordDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WordDictionaryRepository extends JpaRepository<WordDictionary, Long> {
  Optional<WordDictionary> findByConjugation(Conjugation conjugation);

  boolean existsByConjugation(Conjugation conjugation);
}
