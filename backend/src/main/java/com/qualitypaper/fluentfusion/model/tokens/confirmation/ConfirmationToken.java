package com.qualitypaper.fluentfusion.model.tokens.confirmation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qualitypaper.fluentfusion.model.user.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "confirmation_token")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String confirmationToken;
  @CreatedDate
  @JsonIgnore
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
  @Builder.Default
  private LocalDateTime expires = LocalDateTime.now().plusMinutes(15);
  @JsonIgnore
  @Nullable
  private LocalDateTime confirmedAt;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = false, name = "user_id")
  private User user;
}
