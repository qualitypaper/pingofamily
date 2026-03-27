package com.qualitypaper.fluentfusion.model.vocabulary.word;

import com.qualitypaper.fluentfusion.util.interfaces.Copy;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(indexes = {
        @Index(name = "w_from_idx", columnList = "word_from_id"),
        @Index(name = "w_to_idx", columnList = "word_to_id")
})
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WordTranslation implements Serializable, Copy<WordTranslation> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "word_from_id")
  private Word wordFrom;
  @ManyToOne
  @JoinColumn(name = "word_to_id")
  private Word wordTo;
  @Enumerated(EnumType.STRING)
  private WordTranslationType wordTranslationType;
  private LocalDateTime createdAt;

  public WordTranslation(Long id) {
    this.id = id;
  }
}
