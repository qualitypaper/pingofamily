package com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.ConjugationJsonEntity;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Conjugation implements Serializable {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @OneToOne
  @JoinColumn(name = "word_id", referencedColumnName = "id")
  private Word word;
  @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REMOVE})
  @JoinColumn(name = "conjugation_json_id", referencedColumnName = "id")
  private ConjugationJsonEntity conjugationJsonEntity;
  @JsonIgnore
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime createdAt;
}
