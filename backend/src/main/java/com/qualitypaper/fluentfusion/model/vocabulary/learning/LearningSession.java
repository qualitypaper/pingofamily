package com.qualitypaper.fluentfusion.model.vocabulary.learning;

import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroup;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LearningSession implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToMany
  @JoinTable(
          name = "learning_session_learning_words",
          joinColumns = @JoinColumn(name = "learning_session_id"),
          inverseJoinColumns = @JoinColumn(name = "user_vocabulary_id")
  )
  private List<UserVocabulary> learningWords;
  @CreatedBy
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
  @ManyToOne
  @JoinColumn(name = "vocabulary_group_id")
  private VocabularyGroup vocabularyGroup;
  @ManyToOne
  @JoinColumn(name = "vocabulary_id")
  private Vocabulary vocabulary;
  private LocalDateTime completedAt;
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
}
