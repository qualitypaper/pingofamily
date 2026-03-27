package com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.Training;
import com.qualitypaper.fluentfusion.util.interfaces.ToMap;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingExample implements ToMap, Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "training_example_data_id")
  private TrainingExampleData trainingExampleData;
  @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH}, mappedBy = "trainingExample")
  private List<TrainingExampleStatistics> trainingExampleStatistics;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "training_id", nullable = false)
  private Training training;
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();

}
