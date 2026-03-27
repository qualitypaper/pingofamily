package com.qualitypaper.fluentfusion.model.vocabulary.learning.training;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TranslationJsonEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String translation;
  @Enumerated(EnumType.STRING)
  private PartOfSpeech partOfSpeech;
}
