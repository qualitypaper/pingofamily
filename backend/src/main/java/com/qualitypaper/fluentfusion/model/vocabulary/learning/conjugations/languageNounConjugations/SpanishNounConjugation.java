package com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.languageNounConjugations;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.NounMappings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class SpanishNounConjugation extends NounMappings {

  // singularMasculine and pluralMasculine are already filled in NounConjugation class
  @Column(name = "singular_feminine")
  private String singularFeminine;
  @Column(name = "plural_feminine")
  private String pluralFeminine;

}
