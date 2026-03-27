package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.Desc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescRepository extends JpaRepository<Desc, Long> {
  List<Desc> findByDescription(String description);
}