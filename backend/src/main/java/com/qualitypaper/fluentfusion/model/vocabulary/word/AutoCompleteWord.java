package com.qualitypaper.fluentfusion.model.vocabulary.word;

import com.qualitypaper.fluentfusion.model.Language;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"word", "language"})
        },
        indexes = {
                @Index(name = "a_c_id_idx", columnList = "id"),
                @Index(name = "a_c_word_lang_idx", columnList = "word, language")
        }

)
public class AutoCompleteWord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String word;
  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "possible_translation_id")
  private List<PossibleTranslationByLanguage> possibleTranslation;
  @Enumerated(EnumType.STRING)
  private Language language;
}
