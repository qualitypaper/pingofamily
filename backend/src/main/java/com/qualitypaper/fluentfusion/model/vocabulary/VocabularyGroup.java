package com.qualitypaper.fluentfusion.model.vocabulary;


import com.qualitypaper.fluentfusion.model.vocabulary.learning.LearningSession;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class VocabularyGroup implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String imageUrl;
  @Enumerated(EnumType.STRING)
  private VocabularyGroupType type;
  @Builder.Default
  @Enumerated(EnumType.STRING)
  private Difficulty difficulty = Difficulty.EASY;
  @ManyToOne
  @JoinColumn(name = "vocabulary_id")
  private Vocabulary vocabulary;
  @OneToMany(mappedBy = "vocabularyGroup", cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH})
  private List<UserVocabulary> words;
  @OneToMany(mappedBy = "vocabularyGroup", cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE})
  private List<LearningSession> learningSessions;
  @Builder.Default
  // used by admins, mainly for predefined vocabulary groups,
  // to filter and check the group before publishing it to the public
  @Column(columnDefinition = "boolean default true")
  private Boolean activated = true;
  @Builder.Default
  @Column(columnDefinition = "boolean default false")
  private Boolean deleted = false;
  private LocalDateTime createdAt;
}
