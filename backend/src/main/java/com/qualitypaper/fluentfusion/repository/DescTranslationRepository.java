package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.Desc;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.DescTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescTranslationRepository extends JpaRepository<DescTranslation, Long> {
  List<DescTranslation> findByDesc(Desc desc);
}