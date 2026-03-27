package com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary;

import com.qualitypaper.fluentfusion.buffers.Weights;
import com.qualitypaper.fluentfusion.controller.dto.request.AddTranslationRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.AddWordRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.RegenerateExamplesRequest;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.*;
import com.qualitypaper.fluentfusion.exception.VocabularyOwnerException;
import com.qualitypaper.fluentfusion.exception.notfound.UserVocabularyNotFoundException;
import com.qualitypaper.fluentfusion.mappers.userVocabulary.UserVocabularyMapper;
import com.qualitypaper.fluentfusion.mappers.word.WordMapper;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.user.Role;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.*;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.model.vocabulary.word.*;
import com.qualitypaper.fluentfusion.repository.userVocabulary.UserVocabularyRepository;
import com.qualitypaper.fluentfusion.service.db.queries.resultTypes.GetWords;
import com.qualitypaper.fluentfusion.service.pts.examples.ExamplesGenerationService;
import com.qualitypaper.fluentfusion.service.pts.examples.ExamplesResponse;
import com.qualitypaper.fluentfusion.service.serialization.WeightsSerializationService;
import com.qualitypaper.fluentfusion.service.socket.SocketEventType;
import com.qualitypaper.fluentfusion.service.socket.SocketService;
import com.qualitypaper.fluentfusion.service.socket.room.RoomCodeGenerationFactory;
import com.qualitypaper.fluentfusion.service.user.UserDbService;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.WordDictionaryService;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.AddWord;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.ExampleTranslationStruct;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabulary.VocabularyDbService;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabularyGroup.VocabularyGroupService;
import com.qualitypaper.fluentfusion.service.vocabulary.word.TranslationJson;
import com.qualitypaper.fluentfusion.service.vocabulary.word.WordService;
import com.qualitypaper.fluentfusion.service.vocabulary.word.WordTranslationService;
import com.qualitypaper.fluentfusion.service.vocabulary.wordExample.WordExampleService;
import com.qualitypaper.fluentfusion.service.vocabulary.wordExample.WordExampleTranslationService;
import com.qualitypaper.fluentfusion.util.StringUtils;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserVocabularyService {
  private final UserVocabularyRepository userVocabularyRepository;
  private final WordService wordService;
  private final WordTranslationService wordTranslationService;
  private final WordExampleService wordExampleService;
  private final WordExampleTranslationService wordExampleTranslationService;
  private final SocketService socketService;
  private final VocabularyGroupService vocabularyGroupService;
  private final UserVocabularyDbService userVocabularyDbService;
  private final UserVocabularyEntityService userVocabularyEntityService;
  private final UserDbService userDbService;
  private final VocabularyDbService vocabularyDbService;
  private final ExamplesGenerationService examplesGenerationService;
  private final UserVocabularyMapper userVocabularyMapper;
  private final RoomCodeGenerationFactory roomCodeGenerationFactory;
  private final WeightsSerializationService weightsSerializationService;
  private final WordMapper wordMapper;

  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    Weights weights;
    try {
      weights = weightsSerializationService.loadDefaultWeights();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    if (weights == null) return;

    Pageable pageable = Pageable.ofSize(30);
    while (pageable != null) {
      pageable = userVocabularyDbService.setWeights(weights, pageable);
    }
  }


  @Transactional
  public WordListResponse addNewWord(AddWordRequest addWordRequest) {
    User user = userDbService.getUser(SecurityContextHolder.getContext());

    VocabularyGroup vocabularyGroup = vocabularyGroupService.findById(addWordRequest.getVocabularyGroupId());
    Vocabulary vocabulary = vocabularyGroup.getVocabulary();
    if (!user.getId().equals(vocabulary.getCreatedBy().getId())) {
      throw new VocabularyOwnerException();
    } else if (vocabularyGroup.getType().equals(VocabularyGroupType.PREDEFINED)
            && !vocabulary.getCreatedBy().getRole().equals(Role.ROLE_ADMIN)) {

      return WordListResponse.builder()
              .status(ResponseStatus.FAIL)
              .error("Permission Denied")
              .build();
    }

    if (!userVocabularyRepository.isVocabularyGroupUnique(vocabularyGroup, addWordRequest.getWord(), addWordRequest.getTranslationJson().getTranslation(), PartOfSpeech.valueOf(addWordRequest.getTranslationJson().getPos()))) {
      return WordListResponse.builder()
              .status(ResponseStatus.FAIL)
              .error("This word already exists in this vocabulary group")
              .build();
    }

    Optional<WordTranslation> alreadyCreated = wordTranslationService.isAlreadyCreated(
            addWordRequest,
            vocabulary.getLearningLanguage(),
            vocabulary.getNativeLanguage()
    );
    log.info("Received add word request with content: {}", addWordRequest);

    if (alreadyCreated.isPresent() && WordTranslationService.checkValidity(alreadyCreated.get())) {

      return addAlreadyCreatedWord(alreadyCreated.get(),
              vocabularyGroup,
              addWordRequest.getTempWordId(),
              SocketEventType.FILL_WORD
      );
    } else CompletableFuture.runAsync(() -> addNewWord(addWordRequest, vocabularyGroup));

    return WordListResponse.builder().status(ResponseStatus.NEW).build();
  }


  public WordListResponse addAlreadyCreatedWord(WordTranslation alreadyCreated,
                                                VocabularyGroup vocabularyGroup,
                                                @Nullable String tempWordId,
                                                SocketEventType socketEventType
  ) {
    CompletableFuture<WordExampleTranslation> wordExampleTranslationFuture = wordExampleTranslationService.createWordExampleTranslation(
            alreadyCreated, vocabularyGroup.getVocabulary()
    );

    wordExampleTranslationFuture.thenAccept(wordExampleTranslation -> {
      UserVocabulary userVocabulary = userVocabularyEntityService.create(
              alreadyCreated, wordExampleTranslation, vocabularyGroup
      );
      sendSocketMessage(userVocabulary, tempWordId, socketEventType);
    });

    Optional<UserVocabulary> previous = userVocabularyRepository.findTopByWordTranslation(alreadyCreated);

    if (previous.isEmpty()) {
      return WordListResponse.builder().status(ResponseStatus.NEW).build();
    } else {
      return userVocabularyMapper.mapFrom(
              vocabularyGroup,
              tempWordId,
              previous.get());
    }
  }


  private void addNewWord(AddWordRequest addWordRequest, VocabularyGroup vocabularyGroup) {
    log.info("Adding word to vocabulary {}", addWordRequest.getWord());
    Vocabulary vocabulary = vocabularyGroup.getVocabulary();
    TranslationJson translationResult = addWordRequest.getTranslationJson();

    WordType wordType = wordService.determineWordType(
            addWordRequest.getWord().toLowerCase(),
            WordDictionaryService.mapPos(translationResult.getPos()),
            vocabulary.getNativeLanguage()
    );

    WordTranslation wordTranslation = wordTranslationService.createAndSave(
            AddWord.builder()
                    .translation(translationResult)
                    .wordType(wordType).wordTranslationType(WordTranslationType.SYSTEM)
                    .word(addWordRequest.getWord())
                    .vocabulary(vocabulary)
                    .user(vocabulary.getCreatedBy())
                    .build()
    );
    log.info("Created word translation for word {}", addWordRequest.getWord());

    if (wordType.equals(WordType.SENTENCE)) {
      UserVocabulary userVocabulary = userVocabularyEntityService.create(wordTranslation, null, vocabularyGroup);
      sendSocketMessage(userVocabulary, addWordRequest.getTempWordId(), SocketEventType.ADD_WORD);
    } else {

      CompletableFuture<WordExampleTranslation> wordExampleTranslationFuture = wordExampleTranslationService.createWordExampleTranslation(
              wordTranslation, vocabularyGroup.getVocabulary()
      );

      wordExampleTranslationFuture.thenAccept(wordExampleTranslation -> {
        log.info("Created word example translation for word {}", addWordRequest.getWord());
        UserVocabulary userVocabulary = userVocabularyEntityService.create(wordTranslation, wordExampleTranslation, vocabularyGroup);
        sendSocketMessage(userVocabulary, addWordRequest.getTempWordId(), SocketEventType.ADD_WORD);
      });
    }
  }

  private void sendSocketMessage(UserVocabulary userVocabulary, String tempId, SocketEventType socketEventType) {
    if (tempId == null || userVocabulary == null || socketEventType == null) {
      return;
    }
    User user = userVocabulary.getVocabulary().getCreatedBy();
    socketService.sendMessage(StringUtils.encodeMD5(user.getEmail()),
            userVocabularyMapper.mapFrom(userVocabulary, tempId),
            socketEventType);
  }

  public void deleteWord(long userVocabularyId) {
    User user = userDbService.getUser(SecurityContextHolder.getContext());

    deleteWord(userVocabularyId, user);
  }

  public void deleteWord(long userVocabularyId, User user) {
    var userVocabulary = userVocabularyRepository.findById(userVocabularyId).orElse(null);
    if (userVocabulary == null) return;
    else if (!checkVocabularyGroupPermission(userVocabulary.getVocabularyGroup(), user)) {
      throw new IllegalStateException("Unable to delete this word. Permission denied");
    }

    userVocabularyDbService.delete(userVocabulary);
  }

  @Async
  @Transactional
  public void changeTranslation(AddTranslationRequest addTranslationRequest) {

    UserVocabulary userVocabulary = userVocabularyRepository.findById(addTranslationRequest.getId()).orElseThrow();
    Vocabulary vocabulary = userVocabulary.getVocabulary();
    VocabularyGroup vocabularyGroup = vocabularyGroupService.findById(addTranslationRequest.getVocabularyGroupId());

    if (vocabularyGroup.getType().equals(VocabularyGroupType.PREDEFINED)
            && !vocabulary.getCreatedBy().getRole().equals(Role.ROLE_ADMIN)) {
      return;
    }

    Word wordFrom = userVocabulary.getWordTranslation().getWordFrom();
    // initializing lazy loaded field for future creation of userVocabulary entity
    Hibernate.initialize(wordFrom.getWordDictionary());

    CompletableFuture<Word> wordTo = wordService.createAndSaveWord(addTranslationRequest.getTranslation(),
            addTranslationRequest.getPartOfSpeech(),
            vocabulary.getNativeLanguage(),
            wordService.determineWordType(
                    addTranslationRequest.getTranslation(),
                    addTranslationRequest.getPartOfSpeech(),
                    vocabulary.getNativeLanguage()
            ),
            true);

    long previousUserVocabularyId = userVocabulary.getId();
    WordTranslation wordTranslation = wordTranslationService.createAndSave(wordFrom, wordTo.join(), WordTranslationType.USER);
    CompletableFuture<WordExampleTranslation> wordExampleTranslationFuture = wordExampleTranslationService.createWordExampleTranslation(
            wordTranslation, vocabularyGroup.getVocabulary()
    );

    wordExampleTranslationFuture.thenAccept(wordExampleTranslation -> {
      userVocabularyDbService.delete(userVocabulary);
      saveUserVocabularyAndSendMessage(previousUserVocabularyId, vocabularyGroup,
              wordTranslation, wordExampleTranslation);
    });


  }

  @Async
  @Transactional
  public void regenerateExample(RegenerateExamplesRequest regenerateExamplesRequest) {

    var userVocabulary = userVocabularyRepository.findById(regenerateExamplesRequest.userVocabularyId())
            .orElseThrow(UserVocabularyNotFoundException::new);

    long previousUserVocabularyId = userVocabulary.getId();
    WordTranslation wordTranslation = userVocabulary.getWordTranslation();
    VocabularyGroup vocabularyGroup = userVocabulary.getVocabularyGroup();
    Vocabulary vocabulary = userVocabulary.getVocabulary();

    List<ExamplesResponse> examples = generateExamples(wordTranslation, vocabulary, regenerateExamplesRequest.difficulty());

    WordExampleTranslation wordExampleTranslation = wordExampleTranslationService.generateSound(mapAndSaveWordExample(userVocabulary, examples));

    userVocabularyDbService.delete(userVocabulary);

    saveUserVocabularyAndSendMessage(previousUserVocabularyId, vocabularyGroup, wordTranslation, wordExampleTranslation);
  }

  private List<ExamplesResponse> generateExamples(WordTranslation wordTranslation, Vocabulary vocabulary, Difficulty difficulty) {
    return examplesGenerationService.generateExamples(
            new ExampleTranslationStruct(
                    wordTranslation.getWordFrom().getWord(),
                    wordTranslation.getWordTo().getWord(),
                    wordTranslation.getWordFrom().getPos(),
                    vocabulary.getLearningLanguage(),
                    vocabulary.getNativeLanguage(),
                    difficulty
            )
    );
  }

  protected void saveUserVocabularyAndSendMessage(long previousUserVocabularyId, VocabularyGroup vocabularyGroup, WordTranslation wordTranslation, WordExampleTranslation wordExampleTranslation) {
    UserVocabulary userVocabularyToSave = userVocabularyEntityService.create(wordTranslation, wordExampleTranslation, vocabularyGroup);

    ChangeWordParametersResponse wordParametersResponse = new ChangeWordParametersResponse(previousUserVocabularyId,
            userVocabularyMapper.mapFrom(userVocabularyToSave, previousUserVocabularyId));

    socketService.sendMessage(
            roomCodeGenerationFactory.generateRoomCode(vocabularyGroup.getVocabulary().getCreatedBy()),
            wordParametersResponse,
            SocketEventType.CHANGE_WORD_PARAMETERS
    );
  }

  private WordExampleTranslation mapAndSaveWordExample(UserVocabulary userVocabulary, List<ExamplesResponse> examplesResponse) {
    String[] examples = getExamplesInOrder(userVocabulary.getVocabulary().getLearningLanguage(), userVocabulary.getVocabulary().getNativeLanguage(), examplesResponse);

    return wordExampleTranslationService.createAndSaveWordExampleTranslation(userVocabulary.getWordTranslation(),
            wordExampleService.createAndSaveWordExample(examples[0], userVocabulary.getWordTranslation().getWordFrom()),
            wordExampleService.createAndSaveWordExample(examples[1], userVocabulary.getWordTranslation().getWordTo())
    );
  }

  public static String[] getExamplesInOrder(Language learningLanguage, Language nativeLanguage, List<ExamplesResponse> examplesResponse) {
    if (examplesResponse == null || examplesResponse.isEmpty()) return new String[]{"", ""};

    String[] examples = new String[2];

    for (ExamplesResponse response : examplesResponse) {
      if (response.language().equals(learningLanguage)) {
        examples[0] = response.example();
      } else if (response.language().equals(nativeLanguage)) {
        examples[1] = response.example();
      }
    }

    return examples;
  }

  public List<WordListResponse> getWordList(long vocabularyGroupId) {
    List<Object[]> wordList = userVocabularyRepository.getWordList(vocabularyGroupId);

    return wordList.stream()
            .map(wordMapper::mapFrom)
            .toList();
  }

  @Transactional(readOnly = true)
  public GetWordsByVocabularyIdResponse getWordWithVocGroup(long vocabularyGroupId) {
    VocabularyGroup vocabularyGroup = vocabularyGroupService.findById(vocabularyGroupId);

    return new GetWordsByVocabularyIdResponse(vocabularyGroupService.formatResponse(vocabularyGroup),
            getWordList(vocabularyGroupId));
  }

  public List<WordListResponse> getAllWordsByVocabularyId(long vocabularyId) {
    User user = userDbService.getUser(SecurityContextHolder.getContext());
    Vocabulary vocabulary = vocabularyDbService.findById(vocabularyId);

    if (!vocabulary.getCreatedBy().getId().equals(user.getId())) {
      throw new VocabularyOwnerException("Wrong vocabulary owner");
    }

    return userVocabularyRepository.findAllByVocabulary(vocabulary)
            .map(userVocabularyMapper::mapFrom)
            .toList();
  }


  @Transactional(readOnly = true)
  public UserVocabularyResponse getWord(long userVocabularyId) {
    UserVocabulary userVocabulary = userVocabularyRepository.findById(userVocabularyId)
            .orElseThrow(UserVocabularyNotFoundException::new);
    // lazy load
    Hibernate.initialize(Objects.requireNonNull(userVocabulary.getWordTranslation().getWordFrom().getWordDictionary()).getSynonyms());

    return userVocabularyMapper.mapFrom(userVocabulary, true);
  }

  private boolean checkVocabularyGroupPermission(VocabularyGroup vocabularyGroup, User user) {
    return user.getRole().equals(Role.ROLE_ADMIN)
            || !vocabularyGroup.getType().equals(VocabularyGroupType.PREDEFINED);
  }

  protected boolean checkUniqueWord(List<GetWords> words, String word, TranslationJson translationJson, long vocabularyGroupId) {
    return words.stream().noneMatch(e -> e.wordFromWord.equals(word)
            && e.vocabularyGroupId == vocabularyGroupId
            && e.wordToWord.equals(translationJson.getTranslation())
            && e.partOfSpeech.equalsIgnoreCase(translationJson.getPos()));
  }


  public static Duration timeFromLastTraining(UserVocabulary userVocabulary) {
    if (userVocabulary.getLastTrainedAt() != null) {
      return Duration.between(userVocabulary.getLastTrainedAt(), LocalDateTime.now());
    } else if (userVocabulary.getCreatedAt() != null) {
      return Duration.between(userVocabulary.getCreatedAt(), LocalDateTime.now());
    }

    throw new IllegalArgumentException("None of the time parameters are filled");
  }
}
