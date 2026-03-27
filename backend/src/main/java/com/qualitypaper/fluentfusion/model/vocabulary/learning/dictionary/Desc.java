package com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "word_desc")
public class Desc implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "_desc", length = 1024)
  private String description;
  @OneToMany(mappedBy = "desc", cascade = CascadeType.ALL)
  private List<DescTranslation> descriptionTranslations;
  private LocalDateTime createdAt;
}
