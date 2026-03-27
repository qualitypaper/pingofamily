package com.qualitypaper.fluentfusion.model.vocabulary;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.Training;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user_vocabulary_statistics")
public class UserVocabularyStatistics implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  @OneToOne
  @JoinColumn(name = "training_id")
  private Training training;
  private Double trainingScore;
  // average difficulty of the training
  private Double averageDifficulty;
  // time difference between this and the previous training
  @Column(name = "training_time_difference_seconds")
  private Duration trainingTimeDifference;
  private LocalDateTime trainingTime;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_vocabulary_id")
  private UserVocabulary userVocabulary;
  // is set only on word deletion
  @ManyToOne
  @JoinColumn(name = "vocabulary_id")
  private Vocabulary vocabulary;
  @Builder.Default
  @Column(name = "deleted_at")
  private LocalDateTime deletedAt = null;

}
