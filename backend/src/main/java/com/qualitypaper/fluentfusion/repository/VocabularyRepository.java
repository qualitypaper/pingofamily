package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
  Optional<Vocabulary> findByLearningLanguageAndNativeLanguageAndCreatedBy(Language learningLanguage, Language nativeLanguage, User user);

  Stream<Vocabulary> findAllByCreatedBy(User user);

  @Query("select v from Vocabulary v where v.learningLanguage=?1 and v.nativeLanguage=?2 and v.createdBy=?3")
  Optional<Vocabulary> findTopByLanguageAndNativeLanguageAndCreatedBy(Language learningLanguage, Language nativeLanguage, User userId);

  Stream<Vocabulary> findAllByLearningLanguageNotInOrNativeLanguageNotIn(Collection<Language> learningLanguages, Collection<Language> nativeLanguages);

}