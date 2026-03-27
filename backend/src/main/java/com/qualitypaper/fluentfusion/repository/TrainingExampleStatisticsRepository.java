package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingExampleStatisticsRepository extends JpaRepository<TrainingExampleStatistics, Long> {
}