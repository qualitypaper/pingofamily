package com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample;

import com.qualitypaper.fluentfusion.service.db.DbService;
import com.qualitypaper.fluentfusion.util.interfaces.ToMap;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingExampleDbService {

  private final EntityManager entityManager;
  private final DbService dbService;


  public List<GetTrainingExample> getTrainingExamples(Long trainingId) {
    Query query = entityManager.createNativeQuery("""
                    select te.id, te.created_at, ted.formatted_string,
                           te.hint, ted.identified_word, ted.sentence,
                           ted.sentence_translation, te.skipped, ted.sound_url,
                           ted.training_type, ted.translation_direction, te.training_id,
                           ted.word_translation_id
                           from training_example te
                    join training_example_data ted on ted.id = te.training_example_data_id
                    where te.training_id=?1
                    """)
            .setParameter(1, trainingId);

    return dbService.getResultList(query, GetTrainingExample.class);
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class GetTrainingExample implements ToMap {
    private Long id;
    private Timestamp createdAt;
    private String formattedString;
    private Boolean hint;
    private String identifiedWord;
    private String sentence;
    private String sentenceTranslation;
    private Boolean skipped;
    private String soundUrl;
    private String trainingType;
    private String translationDirection;
    private Long trainingId;
    private Long wordTranslationId;
  }
}
