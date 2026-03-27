package com.qualitypaper.fluentfusion.model.vocabulary;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WordStatistics implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Builder.Default
  @Column(columnDefinition = "integer default 0")
  private Integer lastWeekTrainingCount = 0;
  @Column(columnDefinition = "integer default 0")
  @Builder.Default
  private Integer lastMonthTrainingCount = 0;
  @Column(columnDefinition = "integer default 0")
  @Builder.Default
  private Integer totalTrainingCount = 0;
  @Column(columnDefinition = "float default 0.0")
  @Builder.Default
  private Double averageTrainingScore = 0.0;
  @OneToOne(mappedBy = "wordStatistics")
  private UserVocabulary userVocabulary;
  @ElementCollection(targetClass = Double.class)
  @CollectionTable(name = "user_vocabulary_weights", joinColumns = @JoinColumn(name = "word_statistics_id"))
  private List<Double> weights = new ArrayList<>();
  @Builder.Default
  private Long createdAt = System.currentTimeMillis();
}