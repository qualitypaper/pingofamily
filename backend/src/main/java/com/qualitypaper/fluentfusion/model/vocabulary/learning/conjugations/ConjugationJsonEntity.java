package com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "conjugation_json_entity")
public class ConjugationJsonEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String infinitive;
  @Enumerated(EnumType.STRING)
  private PartOfSpeech partOfSpeech;
  @JsonIgnore
  private LocalDateTime createdAt;

  public ConjugationJsonEntity(String infinitive, PartOfSpeech partOfSpeech) {
    this.infinitive = infinitive;
    this.partOfSpeech = partOfSpeech;
    this.createdAt = LocalDateTime.now();
  }
}
