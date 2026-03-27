package com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingExampleStatistics {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Builder.Default
  @Column(columnDefinition = "boolean default false")
  private Boolean skipped = false;
  @Builder.Default
  @Column(columnDefinition = "boolean default false")
  private Boolean hint = false;
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
  @ManyToOne
  @JoinColumn(name = "training_example_id")
  private TrainingExample trainingExample;
}
