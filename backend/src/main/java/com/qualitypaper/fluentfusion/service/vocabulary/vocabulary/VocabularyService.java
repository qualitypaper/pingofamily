package com.qualitypaper.fluentfusion.service.vocabulary.vocabulary;

import com.qualitypaper.fluentfusion.controller.dto.request.CreateUserVocabularyRequest;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.VocabularyResponse;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import com.qualitypaper.fluentfusion.repository.LearningSessionRepository;
import com.qualitypaper.fluentfusion.repository.VocabularyRepository;
import com.qualitypaper.fluentfusion.service.db.DbService;
import com.qualitypaper.fluentfusion.service.db.SqlQuery;
import com.qualitypaper.fluentfusion.service.db.queries.GetRecentLearningSessions;
import com.qualitypaper.fluentfusion.service.user.UserDbService;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabularyGroup.VocabularyGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class VocabularyService {


  public static final String DEFAULT = "default";
  private final VocabularyRepository vocabularyRepository;
  private final VocabularyGroupService vocabularyGroupService;
  private final DbService dbService;
  private final VocabularyDbService vocabularyDbService;
  private final UserDbService userDbService;

  public VocabularyResponse formatResponse(Vocabulary vocabulary) {
    return new VocabularyResponse(
            vocabulary.getId(),
            vocabulary.getLearningLanguage(),
            vocabulary.getNativeLanguage(),
            vocabularyGroupService.formatResponse(vocabulary.getVocabularyGroupList()),
            null);
  }

  @Transactional
  public VocabularyResponse create(CreateUserVocabularyRequest createUserVocabularyRequest) {
    User user = userDbService.getUser(SecurityContextHolder.getContext());

    Vocabulary vocabulary = vocabularyDbService.create(createUserVocabularyRequest, user, false);
    return formatResponse(vocabulary);
  }

  @Transactional
  public List<VocabularyResponse> getAllUserVocabularies() {
    User user = userDbService.getUser(SecurityContextHolder.getContext());
    return user.getUserVocabularies().stream().map(this::formatResponse).toList();
  }

  @Transactional
  public void deleteWrongLanguage() {
    List<Language> list = List.of(Language.GERMAN, Language.ENGLISH, Language.SPANISH);
    Stream<Vocabulary> vocabularyList = vocabularyRepository.findAllByLearningLanguageNotInOrNativeLanguageNotIn(list, list);

    vocabularyList.forEach(v -> vocabularyDbService.deleteVocabulary(v.getId()));
  }

  @Transactional(readOnly = true)
  public List<LearningSessionRepository.RecentLearningSession> getRecentLearningSessions(long vocabularyId, long days) {
    User user = userDbService.getUser(SecurityContextHolder.getContext());

    SqlQuery getRecentLearningSessions = new GetRecentLearningSessions(user.getId(), vocabularyId, days + " days");
    return dbService.execute(getRecentLearningSessions);
  }

}
