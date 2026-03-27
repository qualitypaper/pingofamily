package com.qualitypaper.fluentfusion.model.vocabulary;

import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.LearningSession;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "v_idx", columnList = "created_by_id")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@NamedEntityGraph(
        name = "group-list-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("vocabularyGroupList")
        }
)
public class Vocabulary implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Enumerated(EnumType.STRING)
  private Language learningLanguage;
  @Enumerated(EnumType.STRING)
  private Language nativeLanguage;
  private Integer trainingCount;
  private Double averageTrainingScore;
  @Builder.Default
  @OneToMany(mappedBy = "vocabulary", orphanRemoval = true, cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  private List<VocabularyGroup> vocabularyGroupList = new ArrayList<>();
  @Builder.Default
  @OneToMany(mappedBy = "vocabulary", orphanRemoval = true, cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  private List<LearningSession> learningSessions = new ArrayList<>();
  @Builder.Default
  @OneToMany(mappedBy = "vocabulary", orphanRemoval = true, cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
  private List<UserVocabularyStatistics> userVocabularyStatistics = new ArrayList<>();
  @ManyToOne
  @JoinColumn(name = "created_by_id")
  private User createdBy;
  private LocalDateTime createdAt;

  @Override
  public String toString() {
    return "Vocabulary{id=%d, learningLanguage=%s, nativeLanguage=%s}%n".formatted(id, learningLanguage, nativeLanguage);
  }
}
