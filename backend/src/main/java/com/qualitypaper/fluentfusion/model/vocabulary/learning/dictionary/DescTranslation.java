package com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary;

import com.qualitypaper.fluentfusion.model.Language;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DescTranslation implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(length = 1024)
  private String descriptionTranslation;
  @Enumerated(EnumType.STRING)
  private Language language;
  @ManyToOne
  @JoinColumn(name = "desc_id")
  private Desc desc;
}
