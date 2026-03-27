package com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import lombok.*;

import java.util.Map;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AdjectiveConjugation extends ConjugationJsonEntity {

  @ElementCollection(fetch = FetchType.EAGER)
  private Map<String, String> mappings;

  public AdjectiveConjugation(String infinitive, Map<String, String> mappings) {
    super(infinitive, PartOfSpeech.ADJECTIVE);
    this.mappings = mappings;
  }
}
