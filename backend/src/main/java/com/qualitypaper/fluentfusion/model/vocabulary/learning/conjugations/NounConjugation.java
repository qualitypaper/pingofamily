package com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@NoArgsConstructor
@Getter
@Builder
@Setter
@AllArgsConstructor
public class NounConjugation extends ConjugationJsonEntity {

  private String plural;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "noun_mappings_id")
  private NounMappings mapping;

}

