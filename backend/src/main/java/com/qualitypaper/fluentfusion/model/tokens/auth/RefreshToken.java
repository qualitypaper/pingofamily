package com.qualitypaper.fluentfusion.model.tokens.auth;

import com.qualitypaper.fluentfusion.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(length = 1024, unique = true)
  private String refreshToken;
  @OneToMany(mappedBy = "refreshToken", cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
  private List<AuthenticationToken> authenticationTokens;
  private boolean revoked;
  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
  private LocalDateTime createdAt;
  private LocalDateTime expiresAt;
}
