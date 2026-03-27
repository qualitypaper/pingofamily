package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TranslationJsonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranslationJsonEntityRepository extends JpaRepository<TranslationJsonEntity, Long> {
}