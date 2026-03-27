package com.qualitypaper.fluentfusion.model.vocabulary.word;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qualitypaper.fluentfusion.model.Language;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(indexes = {
        @Index(name = "w_e_idx", columnList = "id, example, language")
})
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WordExample implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(length = 500)
  private String example;
  private Language language;
  private String soundUrl;
  @ManyToOne
  @JoinColumn(name = "word_id", referencedColumnName = "id")
  @JsonIgnore
  private Word word;
  @JsonIgnore
  private LocalDateTime createdAt;
}
