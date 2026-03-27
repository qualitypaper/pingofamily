package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExample;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleData;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingExampleRepository extends JpaRepository<TrainingExample, Long> {
  List<TrainingExample> findAllByTrainingExampleData(TrainingExampleData trainingExampleData);

  List<TrainingExample> findAllByTrainingExampleStatistics(TrainingExampleStatistics trainingExampleStatistics);
}
