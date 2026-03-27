package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.vocabulary.word.PossibleTranslationByLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PossibleTranslationByLanguageRepository extends JpaRepository<PossibleTranslationByLanguage, Long> {
}