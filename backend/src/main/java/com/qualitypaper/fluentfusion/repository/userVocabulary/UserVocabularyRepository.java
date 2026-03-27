package com.qualitypaper.fluentfusion.repository.userVocabulary;

import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroup;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.Training;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TrainingType;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import com.qualitypaper.fluentfusion.service.recall.RecallProbability;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserVocabularyRepository extends JpaRepository<UserVocabulary, Long> {

        @Transactional(readOnly = true)
        Optional<UserVocabulary> findById(long id);

        @EntityGraph("training-entity-graph")
        @Query("select uv from UserVocabulary uv where uv.id in (:learningWordsIds)")
        @Transactional(readOnly = true)
        List<UserVocabulary> findTrainedWords(List<Long> learningWordsIds);

        @Transactional(readOnly = true)
        @Query(value = """
                                select uv from UserVocabulary  uv
                                where uv.vocabularyGroup = :vocabularyGroup
                                    and uv.deleted = false
                                    and uv.loading = false
                                    and uv.id in :ids
                        """)
        List<UserVocabulary> findAllByVocabularyGroupAndIds(VocabularyGroup vocabularyGroup, List<Long> ids);

        @Transactional(readOnly = true)
        List<UserVocabulary> findAllByVocabularyGroupAndIsNew(VocabularyGroup vocabularyGroup, Boolean isNew,
                        Pageable pageable);

        @Query(value = """
                        SELECT uv.*
                        FROM user_vocabulary uv
                                 JOIN word_statistics ws on ws.id = uv.word_statistics_id
                                 JOIN word_translation wt ON uv.word_translation_id = wt.id
                                 JOIN word wf ON wt.word_from_id = wf.id
                        WHERE uv.vocabulary_group_id = :vocabularyGroupId
                          AND uv.next_training_id IS NOT NULL
                          AND uv.loading = false
                          AND uv.is_new = false
                          AND uv.deleted = false
                        ORDER BY make_prediction(ws, now() - coalesce(uv.last_trained_at, uv.created_at))
                        """, nativeQuery = true)
        @Transactional(readOnly = true)
        Page<UserVocabulary> findAllByVocabularyGroupAndPriority(long vocabularyGroupId, Pageable first);

        @Query(value = """
                        select uv.id, w_f.word, w_f.sound_url, w_f.pos, w_t.word from user_vocabulary uv
                          join word_translation wt on wt.id = uv.word_translation_id
                          join word w_t on w_t.id = wt.word_to_id
                          join word w_f on w_f.id = wt.word_from_id
                        where uv.vocabulary_group_id = :vocabularyGroupId
                          order by uv.id desc
                        """, nativeQuery = true)
        @Transactional(readOnly = true)
        List<Object[]> getWordList(long vocabularyGroupId);

        @Transactional(readOnly = true)
        Optional<UserVocabulary> findTopByWordTranslation(WordTranslation wordTranslation);

        @Transactional(readOnly = true)
        List<UserVocabulary> findAllByWordTranslation(WordTranslation wordTranslation);

        @Transactional(readOnly = true)
        Stream<UserVocabulary> findAllByVocabulary(Vocabulary vocabulary);

        @Query("""
                        select uv.id from UserVocabulary uv where uv.vocabularyGroup.id=?1
                        """)
        @Transactional(readOnly = true)
        List<Long> findAllForDeletion(long vocabularyGroupId);

        @Modifying
        @Query(value = """
                        INSERT INTO user_vocabulary(
                                  created_at, deleted, is_new,
                                  last_trained_at, loading, well_known_word, next_training_id, vocabulary_id,
                                  vocabulary_group_id, word_example_translation_id, word_translation_id, word_statistics_id
                        )
                        select now(), false, true, now(),
                            false, false, next_training_id, :vocabularyId, :vocabularyGroupId,
                        word_example_translation_id, word_translation_id, insert_into_word_statistics()
                        from user_vocabulary uv
                        where uv.vocabulary_group_id=:predefinedVocabularyGroupId
                        """, nativeQuery = true)
        void insertPredefined(@Param("predefinedVocabularyGroupId") long predefinedVocabularyGroupId,
                        @Param("vocabularyId") long vocabularyId,
                        @Param("vocabularyGroupId") long vocabularyGroupId);

        @Query("""
                            SELECT UserVocabulary FROM UserVocabulary uv ORDER BY uv.id LIMIT :limit OFFSET :offset
                        """)
        @Transactional(readOnly = true)
        List<UserVocabulary> findAllWithPagination(int limit, int offset);

        @Query("""
                                  SELECT uv.nextTraining FROM UserVocabulary uv
                                      JOIN uv.nextTraining nt
                                      JOIN nt.trainingExamples te
                                  WHERE te.trainingExampleData.trainingType = :trainingType
                                  ORDER BY uv.id LIMIT :limit OFFSET :offset
                        """)
        @Transactional(readOnly = true)
        List<Training> findByTrainingType(@Param("trainingType") TrainingType trainingType, @Param("limit") int limit,
                        @Param("offset") int offset);

        @Transactional(readOnly = true)
        List<UserVocabulary> findAllByNextTraining(Training nextTraining);

        @Query(value = """
                        select count(uv) = 0 from UserVocabulary uv
                        where uv.vocabularyGroup = :vocabularyGroup
                        and uv.wordTranslation.wordFrom.word = :wordFrom
                        and uv.wordTranslation.wordTo.word = :wordTo
                        and uv.wordTranslation.wordFrom.pos = :pos
                        """)
        boolean isVocabularyGroupUnique(VocabularyGroup vocabularyGroup, String wordFrom, String wordTo,
                        PartOfSpeech pos);

        @Query(value = """
                select uv.id, make_prediction(ws, now() - coalesce(uv.last_trained_at, uv.created_at))
                        from user_vocabulary uv
                        join word_statistics ws on ws.id = uv.word_statistics_id
                        where uv.is_new = false 
                                and uv.next_training_id <> null
                                and uv.vocabulary_group_id = :vocabularyGroupId
                        """, nativeQuery = true)
        List<RecallProbability> getRecallProbabilities(long vocabularyGroupId);

    long countAllByVocabularyGroupAndIsNew(VocabularyGroup vocabularyGroup, Boolean isNew);
}
