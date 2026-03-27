package com.qualitypaper.fluentfusion.service.script;

import com.qualitypaper.fluentfusion.controller.dto.request.CreateUserVocabularyRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.GeneratePredefinedRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.ReverseVocabularyGroupRequest;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.GetWordsByVocabularyIdResponse;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.WordListResponse;
import com.qualitypaper.fluentfusion.exception.notfound.VocabularyNotFoundException;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.*;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordExample;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordExampleTranslation;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import com.qualitypaper.fluentfusion.repository.VocabularyRepository;
import com.qualitypaper.fluentfusion.repository.WordTranslationRepository;
import com.qualitypaper.fluentfusion.service.db.DbService;
import com.qualitypaper.fluentfusion.service.db.types.Compare;
import com.qualitypaper.fluentfusion.service.db.types.Join;
import com.qualitypaper.fluentfusion.service.email.FormResendService;
import com.qualitypaper.fluentfusion.service.user.UserService;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.UserVocabularyScriptService;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.UserVocabularyService;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabulary.VocabularyDbService;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabulary.VocabularyService;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabularyGroup.VocabularyGroupDbService;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabularyGroup.VocabularyGroupService;
import com.qualitypaper.fluentfusion.service.vocabulary.word.WordService;
import com.qualitypaper.fluentfusion.service.vocabulary.word.WordTranslationService;
import com.qualitypaper.fluentfusion.service.vocabulary.wordExample.WordExampleService;
import com.qualitypaper.fluentfusion.service.vocabulary.wordExample.WordExampleTranslationService;
import com.qualitypaper.fluentfusion.util.FileUtils;
import com.qualitypaper.fluentfusion.util.types.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class WordsAddingScript {

  private final VocabularyGroupService vocabularyGroupService;
  private final UserVocabularyService userVocabularyService;
  private final DbService dbService;
  private final WordTranslationService wordTranslationService;
  private final WordExampleTranslationService wordExampleTranslationService;
  private final WordService wordService;
  private final WordExampleService wordExampleService;
  private final FormResendService formResendService;
  private final UserVocabularyScriptService userVocabularyScriptService;
  private final WordTranslationRepository wordTranslationRepository;
  private final VocabularyRepository vocabularyRepository;
  private final VocabularyDbService vocabularyDbService;
  private final VocabularyGroupDbService vocabularyGroupDbService;


  @Async
  public void launch(GeneratePredefinedRequest request) {
    Language sourceLanguage = request.sourceLanguage();
    Language targetLanguage = request.targetLanguage();

    log.info("Starting parsing from {}", request.filename());

    log.info("Reading file of languages -> from: {}, to: {}", sourceLanguage, targetLanguage);
    VocabularyGroup vocabularyGroup = getVocabularyGroup(request.vocabularyGroupName(), sourceLanguage, targetLanguage);

    try {
      Pair<String[], List<Object[]>> pair = FileUtils.readCsv(request.filename(), request.offset(), request.limit());
      addWords(pair.second(), vocabularyGroup);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }

  }

  private void addWords(List<Object[]> lines, VocabularyGroup vocabularyGroup) {

    for (Object[] line : lines) {
      if (line.length < 2) continue;

      log.info("Adding word: {}", Arrays.toString(line));

      TempWordStorage tempStorage = userVocabularyScriptService.getTempStorage(
              (String) line[0],
              (String) line[1],
              vocabularyGroup
      );

      userVocabularyScriptService.addWordScript(tempStorage);
    }
  }

  public void reversePredefinedVocabularyGroup(ReverseVocabularyGroupRequest request) {
    User user = vocabularyGroupService.isAdmin(SecurityContextHolder.getContext());
    VocabularyGroup vocabularyGroup = vocabularyGroupService.findById(request.initialVocabularyGroupId());

    if (user == null || !vocabularyGroup.getVocabulary().getCreatedBy().getId().equals(user.getId())) {
      throw new IllegalArgumentException("You don't have permission to reverse this vocabulary group");
    }

    reverseVocabularyGroup(vocabularyGroup, user);
  }

  private void reverseVocabularyGroup(VocabularyGroup vocabularyGroup, User user) {
    Language futureLearningLanguage = vocabularyGroup.getVocabulary().getNativeLanguage();
    Language futureNativeLanguage = vocabularyGroup.getVocabulary().getLearningLanguage();

    Vocabulary vocabulary = vocabularyRepository.findByLearningLanguageAndNativeLanguageAndCreatedBy(
            futureLearningLanguage, futureNativeLanguage, user
    ).orElseThrow(VocabularyNotFoundException::new);

    if (vocabulary == null) {
      vocabulary = vocabularyDbService.create(
              new CreateUserVocabularyRequest(
                      futureLearningLanguage.name(),
                      futureNativeLanguage.name()
              ),
              user,
              true
      );
    }

    VocabularyGroup newVocabularyGroup = vocabularyGroupDbService.create(vocabularyGroup.getName(), vocabulary,
            VocabularyGroupType.PREDEFINED,
            user.getUserLevel());

    CompletableFuture.runAsync(() -> reverseWords(vocabularyGroup, newVocabularyGroup));
  }

  private void reverseWords(VocabularyGroup vocabularyGroup, VocabularyGroup newVocabularyGroup) {
    formResendService.sendErrorMessage("Started reversing vocabulary group script -> name: " + vocabularyGroup.getName() + ", id: " + vocabularyGroup.getId());
    GetWordsByVocabularyIdResponse allWords = userVocabularyService.getWordWithVocGroup(vocabularyGroup.getId());

    for (WordListResponse word : allWords.words()) {
      Long futureWordFromId = word.getWordToId();
      Long futureWordToId = word.getWordFromId();
      long wordTranslationId = wordTranslationService.getWordTranslationIdByWords(word.getWordTo(), word.getWordFrom());
      WordTranslation wordTranslation;
      if (wordTranslationId == -1) {
        Optional<Word> from = wordService.findById(futureWordFromId);
        Optional<Word> to = wordService.findById(futureWordToId);
        if (from.isEmpty() || to.isEmpty()) {
          throw new IllegalStateException("Word entities mustn't be null");
        }

        wordTranslation = wordTranslationService.createAndSave(from.get(), to.get());
      } else {
        wordTranslation = wordTranslationRepository.findById(wordTranslationId)
                .orElseThrow();
      }
      List<Map<String, Object>> wordExampleTranslationList = dbService.select("user_vocabulary",
              List.of("word_example_translation_id", "word_example_from_id", "word_example_to_id"),
              List.of(Join.left("word_example_translation", "word_example_translation_id",
                      "wet.id", "wet")),
              List.of(Compare.eq("user_vocabulary.id", word.getUserVocabularyId())), "");

      if (wordExampleTranslationList.isEmpty()) {
        throw new IllegalStateException("Word Example Translation list mustn't be empty");
      }

      Optional<WordExample> exampleFrom = wordExampleService.findById((Long) wordExampleTranslationList.getFirst().get("word_example_to_id"));
      Optional<WordExample> exampleTo = wordExampleService.findById((Long) wordExampleTranslationList.getFirst().get("word_example_from_id"));

      if (exampleFrom.isEmpty() || exampleTo.isEmpty()) {
        throw new IllegalStateException("Examples cannot be null");
      }

      WordExampleTranslation wordExampleTranslation = wordExampleTranslationService.createAndSaveWordExampleTranslation(wordTranslation, exampleFrom.get(), exampleTo.get());

      // TODO: finish the script refactoring
//                UserVocabulary userVocabulary = userVocabularyEntityService.create(wordTranslation, wordExampleTranslation, newVocabularyGroup);
//                log.info("User vocabulary created: {}", userVocabulary);
    }
  }

  @NotNull
  private VocabularyGroup getVocabularyGroup(String vocabularyGroupName, Language sourceLanguage, Language targetLanguage) {
    Vocabulary vocabulary = vocabularyRepository.findByLearningLanguageAndNativeLanguageAndCreatedBy(sourceLanguage, targetLanguage, UserService.WORD_SCRIPT_USER)
            .orElseThrow(VocabularyNotFoundException::new);
    VocabularyGroup vocabularyGroup;

    if (vocabularyGroupName.equals(VocabularyService.DEFAULT)) {
      vocabularyGroup = vocabulary.getVocabularyGroupList().stream().filter(e -> e.getName().equals(VocabularyService.DEFAULT)).toList().getFirst();
    } else {
      vocabularyGroup = vocabularyGroupService.create(vocabulary.getId(),
              vocabularyGroupName, VocabularyGroupType.PREDEFINED, Difficulty.EASY);
    }

    return vocabularyGroup;
  }

}
