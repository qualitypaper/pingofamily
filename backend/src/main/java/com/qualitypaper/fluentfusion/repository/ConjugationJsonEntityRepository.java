package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.ConjugationJsonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConjugationJsonEntityRepository extends JpaRepository<ConjugationJsonEntity, Long> {
}