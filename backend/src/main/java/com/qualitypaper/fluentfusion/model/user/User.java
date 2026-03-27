package com.qualitypaper.fluentfusion.model.user;

import com.qualitypaper.fluentfusion.model.tokens.auth.RefreshToken;
import com.qualitypaper.fluentfusion.model.tokens.confirmation.ConfirmationToken;
import com.qualitypaper.fluentfusion.model.tokens.forgotPassword.ForgotPassword;
import com.qualitypaper.fluentfusion.model.vocabulary.Difficulty;
import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Getter
@Table(name = "_user")
@NamedEntityGraph(
        name = "user-vocabularies-entity-graph",
        attributeNodes = {
                @NamedAttributeNode("userVocabularies")
        }
)
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String fullName;
  @Column(unique = true)
  private String email;
  private String password;
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_image_id")
  private UserImage userImage;
  @Enumerated(EnumType.STRING)
  private Role role;
  @Enumerated(EnumType.STRING)
  private AccountCreationType accountCreationType;
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private Difficulty userLevel = Difficulty.EASY;
  private Boolean confirmed;
  @OneToMany(mappedBy = "user", cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
  private List<RefreshToken> refreshTokens;
  @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
  private List<Vocabulary> userVocabularies;
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<ConfirmationToken> confirmationTokens;
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<ForgotPassword> forgotPasswords;
  @Embedded
  private UserStreakStats userStreakStats;
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE})
  @JoinColumn(name = "vocabulary_id")
  private Vocabulary lastPickedVocabulary;
  @OneToOne(cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE})
  @JoinColumn(name = "user_settings_id")
  private UserSettings userSettings;
  private LocalDateTime lastActiveAt;
  private LocalDateTime createdAt;

  @Override
  public Set<? extends GrantedAuthority> getAuthorities() {
    return Set.of(new SimpleGrantedAuthority(role.name()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }


}
