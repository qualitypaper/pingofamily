package com.qualitypaper.fluentfusion.model.tokens.auth;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authentication_token")
public class AuthenticationToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(length = 1028, unique = true)
  private String token;
  @ManyToOne
  @JoinColumn(name = "refresh_token_id")
  private RefreshToken refreshToken;
  private boolean revoked;
  private boolean expired;
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
  @Builder.Default
  private LocalDateTime lastLogin = LocalDateTime.now();
}