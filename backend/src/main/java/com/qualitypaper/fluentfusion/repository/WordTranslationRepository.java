package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordTranslationRepository extends JpaRepository<WordTranslation, Long> {

  @Transactional(readOnly = true)
  Optional<WordTranslation> findTopByWordFromAndWordTranslationType(Word wordFrom, WordTranslationType wordTranslationType);


  @Transactional(readOnly = true)
  Optional<WordTranslation> findTopByWordFromAndWordToAndWordTranslationType(Word wordFrom, Word wordTo,
                                                                             WordTranslationType wordTranslationType);

  @Query(nativeQuery = true, value = """
          select wt.id from word_translation wt
            left join word w_from on wt.word_from_id = w_from.id
            left join word w_to on wt.word_to_id = w_to.id
          where w_from.word = :wordFrom and w_to.word = :wordTo
          """)
  @Transactional(readOnly = true)
  List<Long> findByWordFromAndWordTo(@Param("wordFrom") String wordFrom, @Param("wordTo") String wordTo);

  @Query("""
          select wt from WordTranslation wt
          where wt.wordFrom = :word or wt.wordTo = :word
          """)
  @Transactional(readOnly = true)
  List<WordTranslation> findAllByWord(@Param("word") Word word);
}
