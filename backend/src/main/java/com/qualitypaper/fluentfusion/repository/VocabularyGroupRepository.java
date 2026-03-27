package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocabularyGroupRepository extends JpaRepository<VocabularyGroup, Long> {

  @Transactional(readOnly = true)
  Optional<VocabularyGroup> findById(long id);

  @Query("""
          select vg from VocabularyGroup vg \
          left join Vocabulary v on vg.vocabulary.id = v.id \
          where v.learningLanguage = ?1 and v.nativeLanguage = ?2 and v.createdBy = ?3
          """)
  List<VocabularyGroup> findAllByLearningLanguageAndNativeLanguage(Language learningLanguage, Language nativeLanguage, User user);


  // TODO: specify entity graph
  @Query("""
          select distinct vg from VocabularyGroup vg join vg.vocabulary v \
          where vg.type = 'PREDEFINED' and vg.activated = true and v.learningLanguage=:learningLanguage \
          and v.nativeLanguage=:nativeLanguage \
          order by vg.id
          """)
  List<VocabularyGroup> findAllPredefinedByLearningLanguageAndNativeLanguage(Language learningLanguage,
                                                                             Language nativeLanguage,
                                                                             Pageable pageable);

}
