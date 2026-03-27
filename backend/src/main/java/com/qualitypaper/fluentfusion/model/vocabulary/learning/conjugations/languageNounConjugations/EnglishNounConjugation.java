package com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.languageNounConjugations;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.NounMappings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;


@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class EnglishNounConjugation extends NounMappings {

  private String possessive;
  @Column(name = "plural_possesive")
  private String pluralPossessive;
}
