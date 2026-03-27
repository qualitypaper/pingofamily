package com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.Conjugation;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Entity
public class VerbConjugation extends ConjugationJsonEntity {


  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "tense_conjugation_id")
  private List<VerbTenseConjugation> verbTenseConjugation;
  @ManyToOne
  @JoinColumn(name = "conjugation_id")
  private Conjugation conjugation;

}