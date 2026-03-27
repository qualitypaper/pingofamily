package com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.languageNounConjugations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "inner_map")
public class InnerMap {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String mapKey;
  @ManyToMany(fetch = FetchType.EAGER)
  private List<KeyValue> valueList;
  @JsonIgnore
  private LocalDateTime createdAt;
}
