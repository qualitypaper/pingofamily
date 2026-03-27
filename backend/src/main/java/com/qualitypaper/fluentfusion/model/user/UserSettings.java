package com.qualitypaper.fluentfusion.model.user;

import com.qualitypaper.fluentfusion.model.Language;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSettings {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Enumerated(EnumType.STRING)
  private Language userInterfaceLanguage = Language.ENGLISH;
  @Column(columnDefinition = "int default 50")
  private Integer wordsPerPage = 50;
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "id", unique = true, nullable = false)
  private User user;

}