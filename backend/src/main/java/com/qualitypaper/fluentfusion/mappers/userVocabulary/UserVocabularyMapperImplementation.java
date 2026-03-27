package com.qualitypaper.fluentfusion.mappers.userVocabulary;

import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.ResponseStatus;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.UserVocabularyResponse;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.WordListResponse;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.WordTranslationResponse;
import com.qualitypaper.fluentfusion.mappers.wordExampleTranslation.WordExampleTranslationMapper;
import com.qualitypaper.fluentfusion.mappers.wordTranslation.WordTranslationMapper;
import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserVocabularyMapperImplementation implements UserVocabularyMapper {

  private final WordTranslationMapper wordTranslationMapper;
  private final WordExampleTranslationMapper wordExampleTranslationMapper;

  @Value("${url.static.audio}")
  private String staticAudioUrl;

  @Override
  public UserVocabularyResponse mapFrom(UserVocabulary userVocabulary, boolean nullTraining) {
    return UserVocabularyResponse.builder()
            .userVocabularyId(userVocabulary.getId())
            .userID(userVocabulary.getVocabulary().getCreatedBy().getId())
            .nextTraining(nullTraining ? null : userVocabulary.getNextTraining())
            .vocabularyGroupId(userVocabulary.getVocabularyGroup().getId())
            .wordTranslation(wordTranslationMapper.mapFrom(userVocabulary.getWordTranslation()))
            .wordExampleTranslation(wordExampleTranslationMapper.mapFrom(userVocabulary.getWordExampleTranslation()))
            .build();
  }

  @Override
  public WordListResponse mapFrom(UserVocabulary userVocabulary, String tempId) {
    return WordListResponse.builder()
            .tempWordId(tempId)
            .userVocabularyId(userVocabulary.getId())
            .vocabularyGroupId(userVocabulary.getVocabularyGroup().getId())
            .wordFrom(userVocabulary.getWordTranslation().getWordFrom().getWord())
            .wordFromId(userVocabulary.getWordTranslation().getWordFrom().getId())
            .wordTo(userVocabulary.getWordTranslation().getWordTo().getWord())
            .wordToId(userVocabulary.getWordTranslation().getWordTo().getId())
            .soundUrl(staticAudioUrl +
                    userVocabulary.getWordTranslation().getWordFrom().getSoundUrl())
            .build();
  }

  @Override
  public WordListResponse mapFrom(UserVocabulary userVocabulary) {
    return WordListResponse.builder()
            .userVocabularyId(userVocabulary.getId())
            .vocabularyGroupId(userVocabulary.getVocabularyGroup().getId())
            .wordFrom(userVocabulary.getWordTranslation().getWordFrom().getWord())
            .wordFromId(userVocabulary.getWordTranslation().getWordFrom().getId())
            .wordTo(userVocabulary.getWordTranslation().getWordTo().getWord())
            .wordToId(userVocabulary.getWordTranslation().getWordTo().getId())
            .soundUrl(staticAudioUrl +
                    userVocabulary.getWordTranslation().getWordFrom().getSoundUrl())
            .build();
  }

  @Override
  public WordListResponse mapFrom(UserVocabulary userVocabulary, long previousUserVocabularyId) {

    return WordListResponse.builder()
            .vocabularyGroupId(userVocabulary.getVocabularyGroup().getId())
            .userVocabularyId(userVocabulary.getId())
            .soundUrl(staticAudioUrl + userVocabulary.getWordTranslation().getWordFrom().getSoundUrl())
            .wordTo(userVocabulary.getWordTranslation().getWordTo().getWord())
            .wordFrom(userVocabulary.getWordTranslation().getWordFrom().getWord())
            .previousUserVocabularyId(previousUserVocabularyId)
            .wordFromId(userVocabulary.getWordTranslation().getWordFrom().getId())
            .wordToId(userVocabulary.getWordTranslation().getWordTo().getId())
            .wordTranslationId(userVocabulary.getWordTranslation().getId())
            .build();
  }

  @Override
  public WordListResponse mapFrom(VocabularyGroup vocabularyGroup,
                                  String tempWordId,
                                  UserVocabulary previous) {

    WordTranslationResponse wordTranslationResponse = wordTranslationMapper.mapFrom(previous.getWordTranslation());

    return WordListResponse.builder()
            .tempWordId(tempWordId)
            .userVocabularyId(previous.getId())
            .vocabularyGroupId(vocabularyGroup.getId())
            .wordTranslationId(wordTranslationResponse.id())
            .wordFromId(wordTranslationResponse.wordFrom().id())
            .wordToId(wordTranslationResponse.wordTo().id())
            .wordFrom(wordTranslationResponse.wordFrom().word())
            .wordTo(wordTranslationResponse.wordTo().word())
            .soundUrl(wordTranslationResponse.wordFrom().soundUrl())
            .status(ResponseStatus.ALREADY_CREATED)
            .build();
  }

}
