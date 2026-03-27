package com.qualitypaper.fluentfusion.service.db.queries;

import com.qualitypaper.fluentfusion.repository.LearningSessionRepository;
import com.qualitypaper.fluentfusion.service.db.SqlQuery;

import java.util.Map;

public record GetRecentLearningSessions(long userId, long vocabularyId, String interval) implements SqlQuery {

  @Override
  public String getQuery() {
    return """
            select id, to_date(cast(ls.completed_at as text), 'yyyy-mm-dd'), vocabulary_id
            from learning_session ls uvs
            left join vocabulary v on ls.vocabulary_id = v.id
                where ls.user_id = :userId and t.completed_at is not null
                and v.created_by_id = :userId and v.id = :vocabularyId
                and extract(epoch from now() - ls.completed_at) < extract(epoch from cast(:interval as interval))
            """;
  }

  @Override
  public Map<String, Object> getParameters() {
    return Map.of(
            "userId", userId,
            "vocabularyId", vocabularyId,
            "interval", interval
    );
  }

  @Override
  public Class<?> getReturnType() {
    return LearningSessionRepository.RecentLearningSession.class;
  }
}
