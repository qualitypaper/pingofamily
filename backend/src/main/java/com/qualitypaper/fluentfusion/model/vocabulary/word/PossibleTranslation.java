package com.qualitypaper.fluentfusion.model.vocabulary.word;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PossibleTranslation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String translation;
  private PartOfSpeech partOfSpeech;
}