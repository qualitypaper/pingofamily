package com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExample;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExampleStatistics;
import com.qualitypaper.fluentfusion.repository.TrainingExampleStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainingExampleStatisticsService {

  private final TrainingExampleStatisticsRepository trainingExampleStatisticsRepository;

  public TrainingExampleStatistics save(TrainingExampleStatistics trainingExampleStatistics) {
    return trainingExampleStatisticsRepository.save(trainingExampleStatistics);
  }

  public TrainingExampleStatistics createAndSave(boolean hint, boolean skipped, TrainingExample trainingExample) {
    TrainingExampleStatistics build = TrainingExampleStatistics.builder()
            .hint(hint)
            .skipped(skipped)
            .trainingExample(trainingExample)
            .build();

    return trainingExampleStatisticsRepository.save(build);
  }
}
