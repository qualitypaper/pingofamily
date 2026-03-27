package com.qualitypaper.fluentfusion.model.vocabulary.word;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.WordDictionary;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(indexes = {
        @Index(name = "w_list_idx", columnList = "word, language, pos"),
        @Index(name = "w_word_idx", columnList = "word"),
        @Index(name = "w_wd_idx", columnList = "word_dictionary_id")
})
@NamedEntityGraph(
        name = "word-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("wordDictionary"),
        }
)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Word implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String word;
  @Enumerated(EnumType.STRING)
  private Language language;
  private String imageUrl;
  private String soundUrl;
  @Enumerated(EnumType.STRING)
  private PartOfSpeech pos;
  @Nullable
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "word_dictionary_id")
  private WordDictionary wordDictionary;
  @Enumerated(EnumType.STRING)
  private WordType wordType;
  @JsonIgnore
  private LocalDateTime createdAt;

  public Word(Long id) {
    this.id = id;
  }

}
