package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.AdjectiveConjugation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdjectiveConjugationRepository extends JpaRepository<AdjectiveConjugation, Long> {
}