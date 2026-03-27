package com.qualitypaper.fluentfusion.model.vocabulary.word;

import com.qualitypaper.fluentfusion.model.Language;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PossibleTranslationByLanguage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @OneToMany(fetch = FetchType.EAGER)
  private List<PossibleTranslation> possibleTranslationList;
  @Enumerated(EnumType.STRING)
  private Language language;
  @ManyToOne
  @JoinColumn(name = "word_id")
  @Nullable
  private Word word;

}