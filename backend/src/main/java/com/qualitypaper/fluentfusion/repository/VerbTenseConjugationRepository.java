package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.VerbTenseConjugation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerbTenseConjugationRepository extends JpaRepository<VerbTenseConjugation, Long> {
}