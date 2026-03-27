package com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TrainingType;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.TranslationDirection;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@NamedEntityGraph(
        name = "training-example-data-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("wordsTranslation")
        }
)
public class TrainingExampleData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "word_translation_id")
  private WordTranslation wordTranslation;
  private String sentence;
  @Column(name = "sentence_translation")
  private String sentenceTranslation;
  @Column(name = "formatted_string")
  private String formattedString;
  @Enumerated(EnumType.STRING)
  private TranslationDirection translationDirection;
  @Column(name = "sound_url")
  private String soundUrl;
  @Column(name = "identified_word")
  private String identifiedWord;
  @Builder.Default
  @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "training_example_data_id")
  private List<TrainingExampleKeys> wordsTranslation = new ArrayList<>();
  @Column(name = "training_type")
  @Enumerated(EnumType.STRING)
  private TrainingType trainingType;
  private LocalDateTime createdAt;
}
