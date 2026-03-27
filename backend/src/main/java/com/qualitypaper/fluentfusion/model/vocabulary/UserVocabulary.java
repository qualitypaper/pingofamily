package com.qualitypaper.fluentfusion.model.vocabulary;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.LearningSession;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.Training;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExample;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordExampleTranslation;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import com.qualitypaper.fluentfusion.util.interfaces.Copy;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Entity
@Table(indexes = {
        @Index(name = "uv_wt_idx", columnList = "word_translation_id"),
        @Index(name = "uv_vg_idx", columnList = "vocabulary_group_id"),
        @Index(name = "uv_v_idx", columnList = "vocabulary_id"),
})
@NamedEntityGraph(
        name = "training-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("nextTraining"),
                @NamedAttributeNode("wordStatistics")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserVocabulary implements Serializable, Copy<UserVocabulary> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "word_translation_id")
  private WordTranslation wordTranslation;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "word_example_translation_id")
  private WordExampleTranslation wordExampleTranslation;
  @ManyToOne
  @JoinColumn(name = "vocabulary_id")
  private Vocabulary vocabulary;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "vocabulary_group_id")
  private VocabularyGroup vocabularyGroup;
  @JoinColumn(name = "word_statistics_id")
  @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.PERSIST})
  private WordStatistics wordStatistics;
  @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "next_training_id")
  private Training nextTraining;
  @Builder.Default
  @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, mappedBy = "userVocabulary")
  private List<UserVocabularyStatistics> trainingStatistics = new ArrayList<>();
  @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
  @Builder.Default
  private Set<TrainingExample> problematicTrainingExamples = new HashSet<>();
  @Builder.Default
  @ManyToMany(mappedBy = "learningWords")
  private List<LearningSession> learningSessions = new ArrayList<>();
  private LocalDateTime lastTrainedAt;
  @Builder.Default
  private Boolean wellKnownWord = false;
  @Builder.Default
  private Boolean isNew = true;
  @Builder.Default
  private Boolean loading = false;
  @Builder.Default
  private Boolean deleted = false;
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();

}
