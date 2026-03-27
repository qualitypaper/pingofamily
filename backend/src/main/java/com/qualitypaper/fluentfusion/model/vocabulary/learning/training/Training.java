package com.qualitypaper.fluentfusion.model.vocabulary.learning.training;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExample;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Training implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "training")
  private List<TrainingExample> trainingExamples;
  @Column(columnDefinition = "boolean default false")
  private Boolean copied;
  private Long createdAt;
  private Long completedAt;


  public void setTrainingExamples(List<TrainingExample> examples) {
    if (this.trainingExamples == null) {
      this.trainingExamples = examples;
    } else {
      this.trainingExamples.clear();
      this.trainingExamples.addAll(examples);
    }
  }
}
