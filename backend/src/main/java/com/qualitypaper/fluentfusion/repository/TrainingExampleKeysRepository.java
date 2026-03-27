package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleKeys;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingExampleKeysRepository extends JpaRepository<TrainingExampleKeys, Integer> {
}