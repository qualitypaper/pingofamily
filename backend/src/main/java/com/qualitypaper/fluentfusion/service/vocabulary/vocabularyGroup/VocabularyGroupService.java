package com.qualitypaper.fluentfusion.service.vocabulary.vocabularyGroup;

import com.qualitypaper.fluentfusion.controller.dto.request.CreateFromPredefinedRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.CreateVocabularyGroupFromLanguagesRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.CreateVocabularyGroupRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.VocabularyGroupUpdateRequest;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.VocabularyGroupResponse;
import com.qualitypaper.fluentfusion.exception.notfound.NotFoundException;
import com.qualitypaper.fluentfusion.exception.notfound.VocabularyGroupNotFoundException;
import com.qualitypaper.fluentfusion.exception.notfound.VocabularyNotFoundException;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.user.Role;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.Difficulty;
import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroup;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroupType;
import com.qualitypaper.fluentfusion.repository.VocabularyGroupRepository;
import com.qualitypaper.fluentfusion.repository.VocabularyRepository;
import com.qualitypaper.fluentfusion.service.user.UserDbService;
import com.qualitypaper.fluentfusion.service.user.UserService;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.UserVocabularyDbService;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabulary.VocabularyService;
import com.qualitypaper.fluentfusion.util.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class VocabularyGroupService {

  public static final int DEFAULT_SUGGESTED_VOCABULARY_GROUP_SELECT_LIMIT = 15;

  private static final Cache<String, List<VocabularyGroup>> suggestedVocabularyGroupsCache = new Cache<>(Language.values().length * (Language.values().length - 1) / 2, 3_600_000);

  private final VocabularyGroupRepository vocabularyGroupRepository;
  private final VocabularyRepository vocabularyRepository;
  private final UserVocabularyDbService userVocabularyDbService;
  private final UserDbService userDbService;
  private final VocabularyGroupDbService vocabularyGroupDbService;

  @Value("${microservice.main.vocabularyGroupImagesFolder}")
  private String vocabularyGroupImagesFolder;
  @Value("${url.static.images}")
  private String staticImagesUrl;

  public VocabularyGroupService(VocabularyGroupRepository vocabularyGroupRepository, VocabularyRepository vocabularyRepository, UserVocabularyDbService userVocabularyDbService, UserDbService userDbService, VocabularyGroupDbService vocabularyGroupDbService, @Qualifier("applicationTaskExecutor") AsyncTaskExecutor asyncTaskExecutor) {
    this.vocabularyGroupRepository = vocabularyGroupRepository;
    this.vocabularyRepository = vocabularyRepository;
    this.userVocabularyDbService = userVocabularyDbService;
    this.userDbService = userDbService;
    this.vocabularyGroupDbService = vocabularyGroupDbService;
  }

  public List<VocabularyGroupResponse> formatResponse(List<VocabularyGroup> vocabularyGroupList) {
    return vocabularyGroupList.stream()
            .filter(e -> !e.getDeleted())
            .map(this::formatResponse)
            .toList();
  }

  public String getImageUrl(String imageFilename) {
    return staticImagesUrl + vocabularyGroupImagesFolder + "/" + imageFilename;
  }

  public VocabularyGroupResponse formatResponse(VocabularyGroup vocabularyGroup) {
    return new VocabularyGroupResponse(vocabularyGroup.getId(), vocabularyGroup.getName(),
            getImageUrl(vocabularyGroup.getImageUrl()),
            vocabularyGroup.getVocabulary().getId(),
            vocabularyGroup.getVocabulary().getLearningLanguage(),
            vocabularyGroup.getVocabulary().getNativeLanguage(),
            vocabularyGroup.getType(), vocabularyGroup.getActivated());
  }

  @Transactional
  public VocabularyGroup create(CreateVocabularyGroupRequest request, VocabularyGroupType type, Difficulty difficulty) {
    Vocabulary vocabulary = vocabularyRepository.findById(request.getVocabularyId())
            .orElseThrow(VocabularyNotFoundException::new);
    if (!vocabulary.getCreatedBy().getRole().equals(Role.ROLE_ADMIN) && type.equals(VocabularyGroupType.PREDEFINED)) {
      throw new IllegalStateException("Only admin can create predefined vocabulary groups");
    }

    return vocabularyGroupDbService.create(request.getName(), vocabulary, type, difficulty);
  }

  @Transactional
  public VocabularyGroup create(long vocabularyId, String name,
                                VocabularyGroupType type, Difficulty difficulty) {
    Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId)
            .orElseThrow(VocabularyNotFoundException::new);
    if (!vocabulary.getCreatedBy().getRole().equals(Role.ROLE_ADMIN) && type.equals(VocabularyGroupType.PREDEFINED)) {
      throw new IllegalStateException("Only admin can create predefined vocabulary groups");
    }

    return vocabularyGroupDbService.create(name, vocabulary, type, difficulty);
  }


  @Transactional
  public VocabularyGroup create(Vocabulary vocabulary, String name, VocabularyGroupType type, Difficulty difficulty) {
    if (!vocabulary.getCreatedBy().getRole().equals(Role.ROLE_ADMIN) && type.equals(VocabularyGroupType.PREDEFINED)) {
      throw new IllegalStateException("Only admin can create predefined vocabulary groups");
    }

    return vocabularyGroupDbService.create(name, vocabulary, type, difficulty);
  }

  public void delete(Long id) {
    Optional<VocabularyGroup> vocabularyGroup = vocabularyGroupRepository.findById(id);
    if (vocabularyGroup.isEmpty()) return;
    User user = vocabularyGroup.get().getVocabulary().getCreatedBy();

    suggestedVocabularyGroupsCache.remove(this.getCacheKey(vocabularyGroup.get().getVocabulary()));

    vocabularyGroupDbService.prepareForDeletion(id, user);
    vocabularyGroupDbService.deleteById(id);
  }

  @Async
  public void deleteAsync(Long id) {
    delete(id);
  }

  @Transactional(readOnly = true)
  public VocabularyGroup findById(Long id) {
    return vocabularyGroupRepository.findById(id)
            .orElseThrow(VocabularyGroupNotFoundException::new);
  }

  @Transactional(readOnly = true)
  public List<VocabularyGroupResponse> getSuggestedVocabularyGroups(int limit, Language learningLanguage,
                                                                    Language nativeLanguage) {
    if (learningLanguage == null || nativeLanguage == null)
      return List.of();
    else if (limit == 0) {
      limit = Integer.MAX_VALUE;
    }

    User user = userDbService.getUser(SecurityContextHolder.getContext());

    Optional<List<VocabularyGroup>> cached = suggestedVocabularyGroupsCache.get(getCacheKey(learningLanguage, nativeLanguage));

    List<VocabularyGroup> list;
    if (cached.isPresent()) {
      list = cached.get();
    } else {
      list = vocabularyGroupRepository.findAllPredefinedByLearningLanguageAndNativeLanguage(
              learningLanguage, nativeLanguage, Pageable.ofSize(limit)
      );

      suggestedVocabularyGroupsCache.put(getCacheKey(learningLanguage, nativeLanguage), list);
    }


    List<VocabularyGroup> predefinedVocabularyGroups;
    if (user == null) {
      predefinedVocabularyGroups = list;
    } else {
      List<Vocabulary> userVocabularies = user.getUserVocabularies();

      Vocabulary vocabulary = userVocabularies.stream()
              .filter(e -> e.getLearningLanguage().equals(learningLanguage)
                      && e.getNativeLanguage().equals(nativeLanguage))
              .findFirst()
              .orElse(null);

      if (vocabulary == null) {
        predefinedVocabularyGroups = list;
      } else {
        predefinedVocabularyGroups = list.stream()
                .filter(e -> vocabulary.getVocabularyGroupList()
                        .stream()
                        .filter(v -> !v.getDeleted())
                        .noneMatch(v -> v.getName().equals(e.getName())
                                && v.getType().equals(VocabularyGroupType.DEFINED_BY_USER_FROM_PREDEFINED)
                                && e.getActivated()
                                && e.getType().equals(VocabularyGroupType.PREDEFINED)))
                .toList();
      }
    }


    return predefinedVocabularyGroups.stream()
            .filter(e -> !e.getName().equals(VocabularyService.DEFAULT))
            .map(this::formatResponse)
            .toList();
  }

  @Transactional(readOnly = true)
  public List<VocabularyGroupResponse> getAllPredefined(Language learningLanguage, Language nativeLanguage) {
    if (learningLanguage == null || nativeLanguage == null)
      return List.of();

    User user = isAdmin(SecurityContextHolder.getContext());
    if (user == null)
      return new ArrayList<>();

    List<VocabularyGroup> predefinedVocabularyGroups = vocabularyGroupRepository
            .findAllPredefinedByLearningLanguageAndNativeLanguage(
                    learningLanguage, nativeLanguage, Pageable.unpaged());

    return predefinedVocabularyGroups.stream()
            .filter(e -> !e.getName().equals(VocabularyService.DEFAULT))
            .map(this::formatResponse)
            .toList();
  }

  @Async
  @Transactional
  public CompletableFuture<Long> createNewVocabularyGroupFromPredefined(CreateFromPredefinedRequest createFromPredefinedRequest) {

    Long vocabularyId = createFromPredefinedRequest.vocabularyId();
    Long predefinedVocabularyGroupId = createFromPredefinedRequest.vocabularyGroupId();
    Vocabulary vocabulary = vocabularyRepository.findById(vocabularyId)
            .orElseThrow(VocabularyNotFoundException::new);

    VocabularyGroup vocabularyGroup = vocabularyGroupRepository.findById(predefinedVocabularyGroupId)
            .orElseThrow(VocabularyGroupNotFoundException::new);

    String vocabularyGroupName;
    long matchingCount = vocabulary.getVocabularyGroupList().stream()
            .filter(e -> !e.getDeleted() && e.getName().equals(vocabularyGroup.getName()))
            .count();
    if (matchingCount != 0) {
      vocabularyGroupName = vocabularyGroup.getName() + (matchingCount + 1);
    } else {
      vocabularyGroupName = vocabularyGroup.getName();
    }
    VocabularyGroup newVocabularyGroup = create(vocabularyId, vocabularyGroupName,
            VocabularyGroupType.DEFINED_BY_USER_FROM_PREDEFINED, vocabularyGroup.getDifficulty()
    );

    userVocabularyDbService.insertWordsFrom(predefinedVocabularyGroupId, vocabularyId, newVocabularyGroup.getId());
    log.info("Successfully inserted words from vocabulary group: {}", predefinedVocabularyGroupId);

    return CompletableFuture.completedFuture(newVocabularyGroup.getId());
  }

  public void deletePredefined(long id) {
    User user = isAdmin(SecurityContextHolder.getContext());
    if (user == null) {
      throw new IllegalCallerException("Wrong user tried to delete predefined vocabulary group");
    }
    vocabularyGroupDbService.prepareForDeletion(id, UserService.WORD_SCRIPT_USER);
    vocabularyGroupDbService.deleteById(id);
  }

  public List<VocabularyGroupResponse> getVocabularyGroups(Language learningLanguage, Language nativeLanguage) {
    User user = isAdmin(SecurityContextHolder.getContext());
    if (user == null)
      return Collections.emptyList();

    return vocabularyGroupRepository
            .findAllByLearningLanguageAndNativeLanguage(learningLanguage, nativeLanguage,
                    UserService.WORD_SCRIPT_USER)
            .stream()
            .map(this::formatResponse)
            .toList();
  }

  @Transactional
  public void updateVocabularyGroup(VocabularyGroupUpdateRequest vocabularyGroupUpdateRequest) {

    VocabularyGroup vocabularyGroup = vocabularyGroupRepository.findById(vocabularyGroupUpdateRequest.vocabularyGroupId())
            .orElseThrow(VocabularyGroupNotFoundException::new);
    User user = userDbService.getUser(SecurityContextHolder.getContext());

    if (user != null && !vocabularyGroup.getVocabulary().getCreatedBy().getId().equals(user.getId())) {
      throw new IllegalArgumentException("Wrong owner tried to change name of another user");
    }

    vocabularyGroup.setName(vocabularyGroupUpdateRequest.newVocabularyGroupName());
    vocabularyGroupRepository.save(vocabularyGroup);
  }

  public User isAdmin(SecurityContext securityContext) {
    User user = userDbService.getUser(securityContext);
    if (user == null) return null;

    return user.getRole().equals(Role.ROLE_ADMIN) ? user : null;
  }

  public VocabularyGroup createPredefinedFromLanguages(CreateVocabularyGroupFromLanguagesRequest request) {
    User user = isAdmin(SecurityContextHolder.getContext());
    if (user == null)
      throw new IllegalStateException("Wrong user tried to create predefined vocabulary group");

    Optional<Vocabulary> vocabulary = vocabularyRepository.findByLearningLanguageAndNativeLanguageAndCreatedBy(request.learningLanguage(),
            request.nativeLanguage(), user);
    if (vocabulary.isEmpty()) {
      throw new NotFoundException("Vocabulary wasn't found in createPredefinedFromLanguages()");
    }

    return create(vocabulary.get().getId(), request.name(),
            VocabularyGroupType.PREDEFINED, Difficulty.EASY);
  }

  private String getCacheKey(Vocabulary voc) {
    return getCacheKey(voc.getLearningLanguage(), voc.getNativeLanguage());
  }

  private String getCacheKey(Language learningLanguage, Language nativeLanguage) {
    return learningLanguage.name() + "," + nativeLanguage.name();
  }

  public static boolean isOwner(VocabularyGroup vocabularyGroup, User user) {
    return user.getId().equals(vocabularyGroup.getVocabulary().getCreatedBy().getId());
  }

}
