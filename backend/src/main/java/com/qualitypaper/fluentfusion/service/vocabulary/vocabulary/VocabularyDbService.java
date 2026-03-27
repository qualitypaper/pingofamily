package com.qualitypaper.fluentfusion.service.vocabulary.vocabulary;

import com.qualitypaper.fluentfusion.controller.dto.request.CreateUserVocabularyRequest;
import com.qualitypaper.fluentfusion.exception.notfound.VocabularyNotFoundException;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroup;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroupType;
import com.qualitypaper.fluentfusion.repository.LearningSessionRepository;
import com.qualitypaper.fluentfusion.repository.UserVocabularyStatisticsRepository;
import com.qualitypaper.fluentfusion.repository.VocabularyRepository;
import com.qualitypaper.fluentfusion.service.user.UserDbService;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabularyGroup.VocabularyGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class VocabularyDbService {

  private final VocabularyRepository vocabularyRepository;
  private final VocabularyGroupService vocabularyGroupService;
  private final LearningSessionRepository learningSessionRepository;
  private final UserVocabularyStatisticsRepository userVocabularyStatisticsRepository;
  private final UserDbService userDbService;

  @Transactional
  public Vocabulary create(CreateUserVocabularyRequest createUserVocabularyRequest, User user, boolean predefined) {
    String uniqueError = "There is an existing vocabulary with learning language " + createUserVocabularyRequest.getLearningLanguage();

    Language learningLanguage = Language.valueOf(createUserVocabularyRequest.getLearningLanguage().toUpperCase());
    Language language = Language.valueOf(createUserVocabularyRequest.getNativeLanguage().toUpperCase());

    if (!checkUnique(user, learningLanguage, language))
      throw new IllegalStateException(uniqueError);

    Vocabulary vocabulary = Vocabulary.builder()
            .averageTrainingScore(0.0)
            .trainingCount(0)
            .learningLanguage(learningLanguage)
            .nativeLanguage(language)
            .createdBy(user)
            .createdAt(LocalDateTime.now())
            .vocabularyGroupList(new ArrayList<>())
            .build();

    vocabularyRepository.save(vocabulary);

    String vocabularyGroupName = predefined ? VocabularyService.DEFAULT : user.getFullName() + "`s VOCABULARY";
    VocabularyGroup vocabularyGroup = vocabularyGroupService.create(vocabulary, vocabularyGroupName, VocabularyGroupType.USER_DEFINED, user.getUserLevel());
    vocabulary.getVocabularyGroupList().add(vocabularyGroup);

    user.setLastPickedVocabulary(vocabulary);
    userDbService.save(user);

    return vocabulary;
  }

  @Transactional
  public void deleteVocabulary(long vocabularyId) {

    Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId)
            .orElseThrow(VocabularyNotFoundException::new);

    Iterator<VocabularyGroup> iterator = vocabulary.getVocabularyGroupList().iterator();

    while (iterator.hasNext()) {
      vocabularyGroupService.delete(iterator.next().getId());
      iterator.remove();
    }

    learningSessionRepository.deleteAllByVocabulary(vocabulary);
    userVocabularyStatisticsRepository.deleteAllByVocabulary(vocabulary);

//    long lastId = vocabulary.getCreatedBy().getLastPickedVocabulary().getId();
//    if (lastId == vocabularyId) {
//      vocabulary.getCreatedBy().setLastPickedVocabulary(null);
//      userService.save(vocabulary.getCreatedBy());
//    }

    vocabularyRepository.delete(vocabulary);
  }

  private boolean checkUnique(User user, Language learningLanguage, Language language) {
    return vocabularyRepository.findByLearningLanguageAndNativeLanguageAndCreatedBy(learningLanguage, language, user).isEmpty();
  }

  public Vocabulary findById(long vocabularyId) {
    return vocabularyRepository.findById(vocabularyId).orElse(null);
  }

  public Optional<Vocabulary> findByIdOptional(long vocabularyId) {
    return vocabularyRepository.findById(vocabularyId);
  }

  public Vocabulary save(Vocabulary vocabulary) {
    return vocabularyRepository.save(vocabulary);
  }

}
