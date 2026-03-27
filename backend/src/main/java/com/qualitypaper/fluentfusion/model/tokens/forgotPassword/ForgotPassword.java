package com.qualitypaper.fluentfusion.model.tokens.forgotPassword;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qualitypaper.fluentfusion.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class ForgotPassword {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String token;
  @CreatedDate
  @JsonIgnore
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
  @JsonIgnore
  @Builder.Default
  private LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false, name = "user_id")
  private User user;
}
