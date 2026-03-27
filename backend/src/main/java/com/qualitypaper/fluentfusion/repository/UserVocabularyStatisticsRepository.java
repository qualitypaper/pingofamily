package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabularyStatistics;
import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserVocabularyStatisticsRepository extends JpaRepository<UserVocabularyStatistics, Long> {

  Optional<UserVocabularyStatistics> findByTraining(Training training);

  List<UserVocabularyStatistics> findAllByUserVocabularyAndTrainingTimeAfter(UserVocabulary userVocabulary, LocalDateTime trainingTimeAfter);

  @Query(nativeQuery = true, value = """
                      select count(*) from user_vocabulary_statistics uvs
                         join training t on uvs.training_id = t.id
                         join training_example te on t.id = te.training_id
                         join training_example_data ted on ted.id = te.training_example_data_id
                      where ted.training_type = :trainingType
                      and uvs.training_time > :date
                      and uvs.user_vocabulary_id = :userVocabularyId
          """)
  long countByTrainingTypeAndTrainingTimeAfter(long userVocabularyId, String trainingType, LocalDateTime date);

  @Modifying
  @Transactional
  @Query(nativeQuery = true, value = """
          UPDATE user_vocabulary_statistics AS uvs
          SET vocabulary_id = uv.vocabulary_id,
              deleted_at = NOW(),
              user_vocabulary_id = NULL
          FROM user_vocabulary uv
          WHERE uvs.user_vocabulary_id = uv.id
            AND uvs.user_vocabulary_id IN (?1)
          """)
  void deleteWithoutStatistics(List<Long> userVocabularyIds);

  void deleteAllByVocabulary(Vocabulary vocabulary);
}
