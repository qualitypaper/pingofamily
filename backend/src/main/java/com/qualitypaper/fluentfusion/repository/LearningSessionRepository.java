package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroup;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.LearningSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface LearningSessionRepository extends JpaRepository<LearningSession, Long> {

  @Query(value = """
              select ls from LearningSession ls \
              where ls.vocabularyGroup=:vocabularyGroup and ls.completedAt = null
          """)
  @Transactional(readOnly = true)
  List<LearningSession> findUnfinishedTrainings(VocabularyGroup vocabularyGroup);

  @Query(value = """
              select new com.qualitypaper.fluentfusion.repository.LearningSessionRepository$RecentLearningSession(ls.id, ls.completedAt, ls.vocabulary.id) \
              from LearningSession ls \
              where ls.user=:user and ls.completedAt > :completedAtAfter
          """)
  List<RecentLearningSession> findByUserAndCompletedAtAfter(User user, LocalDateTime completedAtAfter);

  void deleteAllByVocabulary(Vocabulary vocabulary);


  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class RecentLearningSession {
    private Long id;
    private LocalDateTime completedAt;
    private Long vocabularyId;
  }
}