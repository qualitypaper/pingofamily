package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TrainingType;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleData;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingExampleDataRepository extends JpaRepository<TrainingExampleData, Long> {

  Optional<TrainingExampleData> findTopBySentenceAndSentenceTranslationAndIdentifiedWordAndWordTranslationAndTrainingType(String sentence,
                                                                                                                          String sentenceTranslation,
                                                                                                                          String identifiedWord,
                                                                                                                          WordTranslation wordTranslation,
                                                                                                                          TrainingType trainingType);

  @Query("""
          select ted.id, ted.sentence, ted.translationDirection, ted.wordTranslation
          from TrainingExampleData ted
                      where ted.trainingType <> 'TRANSLATION'
                          and ted.trainingType <> 'AUDIO'
                          and ted.soundUrl = :soundUrl""")
  List<Object[]> findAllBySoundUrl(@Param("soundUrl") String soundUrl);

  List<TrainingExampleData> findAllByWordTranslation(WordTranslation wordTranslation);
}
