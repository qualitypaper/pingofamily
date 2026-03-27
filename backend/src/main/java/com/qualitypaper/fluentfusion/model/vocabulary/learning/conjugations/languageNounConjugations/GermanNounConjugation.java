package com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.languageNounConjugations;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.NounMappings;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class GermanNounConjugation extends NounMappings {

  private String gender;
  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "inner_map_id")
  private List<InnerMap> mappings;


}