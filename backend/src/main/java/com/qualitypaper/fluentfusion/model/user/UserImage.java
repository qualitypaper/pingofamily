package com.qualitypaper.fluentfusion.model.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserImage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String imageUrl;
  @Enumerated(EnumType.STRING)
  private ImageType imageType;
}
