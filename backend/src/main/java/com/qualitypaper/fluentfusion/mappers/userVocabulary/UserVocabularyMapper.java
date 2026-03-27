package com.qualitypaper.fluentfusion.mappers.userVocabulary;

import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.UserVocabularyResponse;
import com.qualitypaper.fluentfusion.controller.dto.response.vocabulary.WordListResponse;
import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroup;

public interface UserVocabularyMapper {

  WordListResponse mapFrom(UserVocabulary userVocabulary);

  WordListResponse mapFrom(UserVocabulary userVocabulary, long previousUserVocabularyId);

  WordListResponse mapFrom(UserVocabulary userVocabulary, String tempId);

  UserVocabularyResponse mapFrom(UserVocabulary userVocabulary, boolean nullTraining);

  WordListResponse mapFrom(VocabularyGroup vocabularyGroup,
                           String tempWordId,
                           UserVocabulary previous);
}
