package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.vocabulary.word.WordExampleTranslation;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordExampleTranslationRepository extends JpaRepository<WordExampleTranslation, Long> {

  int countAllByWordTranslation(WordTranslation wordTranslation);

  List<WordExampleTranslation> getAllByWordTranslation(WordTranslation wordTranslation);
}