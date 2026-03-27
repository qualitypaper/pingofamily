package com.qualitypaper.fluentfusion.service.db.queries;

import com.qualitypaper.fluentfusion.service.db.SqlQuery;
import com.qualitypaper.fluentfusion.service.db.queries.resultTypes.GetWords;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class GetWordsByVocabularyAndVocabularyGroupId implements SqlQuery {

  private final long vocabularyId;
  private final long vocabularyGroupId;

  @Override
  public String getQuery() {
    return """
            select uv.id user_vocabulary_id, wt.id as word_translation_id, w_from.id as word_from_id,
            w_to.id as word_to_id, w_from.word word_from_word, w_to.word word_to_word,
            coalesce(w_from.sound_url, ' ') as sound_url, uv.vocabulary_group_id, w_from.pos,
            uv.created_at
             from user_vocabulary uv
             join word_translation wt on wt.id = uv.word_translation_id
             join word w_from on w_from.id = wt.word_from_id
             join word w_to on w_to.id = wt.word_to_id
             where uv.vocabulary_id=:vocabularyId and uv.vocabulary_group_id=:vocabularyGroupId
             order by uv.created_at
            """;
  }

  @Override
  public Map<String, Object> getParameters() {
    return Map.of(
            "vocabularyId", vocabularyId,
            "vocabularyGroupId", vocabularyGroupId
    );
  }

  @Override
  public Class<?> getReturnType() {
    return GetWords.class;
  }
}
