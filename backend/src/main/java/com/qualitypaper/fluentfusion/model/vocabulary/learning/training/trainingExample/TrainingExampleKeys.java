package com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TranslationJsonEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingExampleKeys implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String key;
  @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REMOVE})
  @JoinColumn(name = "translation_json_id")
  private List<TranslationJsonEntity> values;
}
