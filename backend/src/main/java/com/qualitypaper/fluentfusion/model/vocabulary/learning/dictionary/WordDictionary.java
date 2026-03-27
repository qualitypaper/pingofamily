package com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@ToString
@NamedEntityGraph(
        name = "word-dictionary-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("synonyms"),
        }
)
public class WordDictionary implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
  @JoinColumn(name = "desc_id")
  private Desc desc;
  @ElementCollection
  private List<String> synonyms;
  @JoinColumn(name = "conjugation_id", referencedColumnName = "id")
  @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH})
  private Conjugation conjugation;
  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
