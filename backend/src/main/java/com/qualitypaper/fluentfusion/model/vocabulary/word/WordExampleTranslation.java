package com.qualitypaper.fluentfusion.model.vocabulary.word;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(indexes = {
        @Index(name = "w_e_t_idx", columnList = "id, word_example_from_id, word_example_to_id, word_translation_id")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WordExampleTranslation implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "word_example_from_id", referencedColumnName = "id")
  private WordExample wordExampleFrom;
  @ManyToOne
  @JoinColumn(name = "word_example_to_id")
  private WordExample wordExampleTo;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "word_translation_id")
  private WordTranslation wordTranslation;
  @JsonIgnore
  private LocalDateTime createdAt;
}
