package com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VerbTenseConjugation implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String tense;
  @ElementCollection(fetch = FetchType.EAGER)
  private Map<String, String> tenseConjugations;
}
