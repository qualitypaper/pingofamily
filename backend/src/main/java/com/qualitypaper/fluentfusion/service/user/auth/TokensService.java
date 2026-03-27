package com.qualitypaper.fluentfusion.service.user.auth;

import com.qualitypaper.fluentfusion.model.tokens.auth.AuthenticationToken;
import com.qualitypaper.fluentfusion.model.tokens.auth.RefreshToken;
import com.qualitypaper.fluentfusion.model.tokens.confirmation.ConfirmationToken;
import com.qualitypaper.fluentfusion.model.tokens.forgotPassword.ForgotPassword;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.repository.AuthenticationTokenRepository;
import com.qualitypaper.fluentfusion.repository.ConfirmationTokenRepository;
import com.qualitypaper.fluentfusion.repository.ForgotPasswordRepository;
import com.qualitypaper.fluentfusion.repository.RefreshTokenRepository;
import com.qualitypaper.fluentfusion.util.types.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokensService {

  private final AuthenticationTokenRepository authenticationTokenRepository;
  private final ConfirmationTokenRepository confirmationTokenRepository;
  private final ForgotPasswordRepository forgotPasswordRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  public static String generateRandomString(int length) {
    return UUID.randomUUID().toString().substring(0, length);
  }

  public AuthenticationToken saveAuthenticationToken(String accessToken, RefreshToken refreshToken) {
    AuthenticationToken token = AuthenticationToken.builder()
            .token(accessToken)
            .refreshToken(refreshToken)
            .expired(false)
            .revoked(false)
            .createdAt(LocalDateTime.now())
            .build();

    return authenticationTokenRepository.save(token);
  }

  public RefreshToken saveRefreshToken(String refreshToken, User user) {
    RefreshToken token = RefreshToken.builder()
            .authenticationTokens(new ArrayList<>())
            .user(user)
            .revoked(false)
            .refreshToken(refreshToken)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plus(JwtService.REFRESH_TOKEN_EXPIRATION))
            .build();

    return refreshTokenRepository.save(token);
  }

  public void saveUserForgotPasswordToken(User user, String forgotPasswordToken) {
    var token = ForgotPassword.builder()
            .user(user)
            .token(forgotPasswordToken)
            .expiresAt(LocalDateTime.now().plusMinutes(15))
            .createdAt(LocalDateTime.now())
            .build();
    forgotPasswordRepository.save(token);
  }

  public void saveUserConfirmationToken(User user, String token) {
    Optional<ConfirmationToken> byConfirmationToken = confirmationTokenRepository.findByUser(user);
    if (byConfirmationToken.isEmpty()) {
      createAndSaveConfirmationToken(user, token);
      return;
    }
    var confirmationToken = byConfirmationToken.get();
    confirmationToken.setCreatedAt(LocalDateTime.now());
    confirmationToken.setExpires(LocalDateTime.now().plusMinutes(15));
    confirmationToken.setConfirmationToken(token);
    confirmationTokenRepository.save(confirmationToken);
  }

  private void createAndSaveConfirmationToken(User user, String confirmationToken) {
    var token = ConfirmationToken.builder()
            .user(user)
            .confirmationToken(confirmationToken)
            .createdAt(LocalDateTime.now())
            .expires(LocalDateTime.now().plusMinutes(15))
            .build();
    confirmationTokenRepository.save(token);
  }

  @Transactional
  public void revokeAllUserTokens(User user) {
    List<RefreshToken> validUserTokens = refreshTokenRepository.findAllByUser(user);
    if (validUserTokens.isEmpty())
      return;

    validUserTokens.forEach(refreshToken -> {
      refreshToken.setRevoked(true);
      for (AuthenticationToken authenticationToken : refreshToken.getAuthenticationTokens()) {
        authenticationToken.setExpired(true);
        authenticationToken.setRevoked(true);
      }
    });
    refreshTokenRepository.saveAll(validUserTokens);
  }

  public boolean checkConfirmationTokenExpiration(ConfirmationToken confirmationToken) {
    return checkTokenExpiration(confirmationToken.getExpires());
  }

  public boolean checkForgotPasswordTokenExpiration(ForgotPassword forgotPasswordToken) {
    return checkTokenExpiration(forgotPasswordToken.getExpiresAt());
  }

  private boolean checkTokenExpiration(LocalDateTime expires) {
    LocalDateTime now = LocalDateTime.now();
    Duration difference = Duration.between(now, expires);
    return Math.abs(difference.toMinutes()) < 15;
  }


  @Transactional
  public Pair<RefreshToken, AuthenticationToken> saveTokenPair(JwtService.TokenPair tokenPair, User user) {
    RefreshToken refreshToken = saveRefreshToken(tokenPair.refreshToken(), user);
    AuthenticationToken authToken = saveAuthenticationToken(tokenPair.accessToken(), refreshToken);

    return new Pair<>(refreshToken, authToken);
  }
}
