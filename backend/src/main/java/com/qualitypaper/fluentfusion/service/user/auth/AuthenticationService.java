package com.qualitypaper.fluentfusion.service.user.auth;

import com.qualitypaper.fluentfusion.controller.dto.request.AuthenticationRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.RegisterRequest;
import com.qualitypaper.fluentfusion.controller.dto.response.auth.*;
import com.qualitypaper.fluentfusion.exception.EmailNotValidException;
import com.qualitypaper.fluentfusion.exception.notfound.UserNotFoundException;
import com.qualitypaper.fluentfusion.mappers.user.UserMapper;
import com.qualitypaper.fluentfusion.model.tokens.auth.AuthenticationToken;
import com.qualitypaper.fluentfusion.model.tokens.auth.RefreshToken;
import com.qualitypaper.fluentfusion.model.user.*;
import com.qualitypaper.fluentfusion.model.vocabulary.Difficulty;
import com.qualitypaper.fluentfusion.repository.RefreshTokenRepository;
import com.qualitypaper.fluentfusion.repository.UserRepository;
import com.qualitypaper.fluentfusion.repository.UserSettingsRepository;
import com.qualitypaper.fluentfusion.service.email.EmailFormat;
import com.qualitypaper.fluentfusion.service.email.FormResendService;
import com.qualitypaper.fluentfusion.service.email.NotificationService;
import com.qualitypaper.fluentfusion.service.email.UserChecker;
import com.qualitypaper.fluentfusion.service.socket.room.RoomCodeGenerationFactory;
import com.qualitypaper.fluentfusion.service.user.ProfileImageFormatter;
import com.qualitypaper.fluentfusion.service.user.UserSettingsService;
import com.qualitypaper.fluentfusion.service.user.UserStreakService;
import com.qualitypaper.fluentfusion.util.types.Pair;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final RoomCodeGenerationFactory roomCodeGenerationFactory;
  private final AuthenticationManager authenticationManager;
  private final TokensService tokensService;
  private final UserMapper userMapper;
  private final FormResendService formResendService;
  private final ProfileImageFormatter profileImageFormatter;
  private final RefreshTokenRepository refreshTokenRepository;
  private final UserSettingsRepository userSettingsRepository;
  private final NotificationService notificationService;

  @Transactional
  public AuthenticationResponse register(RegisterRequest request) {
    if (!UserChecker.isValidEmail(request.getEmail()))
      throw new EmailNotValidException("Email is not valid");
    else if (!UserChecker.isValidPassword(request.getPassword()))
      throw new IllegalArgumentException("Password is not valid");

    User user = fromRequest(request);

    if (repository.findByEmail(user.getEmail()).isPresent())
      throw new IllegalStateException("User already exists with such email");
    userSettingsRepository.save(user.getUserSettings());
    repository.save(user);

    JwtService.TokenPair tokenPair = jwtService.generateTokenPair(user.getId(), user.getUsername());
    Pair<RefreshToken, AuthenticationToken> _ = tokensService.saveTokenPair(tokenPair, user);

    formResendService.sendInfoMessage("New User registered with email: " + user.getEmail());
    notificationService.sendWelcomeEmail(request.getEmail(), request.getFullName());

    return new AuthenticationResponse(
            new UserDetailsResponse(
                    user.getEmail(),
                    user.getFullName(),
                    roomCodeGenerationFactory.generateRoomCode(user),
                    profileImageFormatter.format(user.getUserImage()),
                    null
            ),
            new TokenResponse(
                    tokenPair.refreshToken(),
                    tokenPair.accessToken()
            ),
            new SettingsResponse(
                    user.getUserSettings().getUserInterfaceLanguage(),
                    user.getUserSettings().getWordsPerPage()
            ),
            new StreakResponse(
                    0, 0
            )
    );
  }

  @Transactional
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    if (Objects.isNull(request.getPassword()) || Objects.isNull(request.getEmail()))
      throw new IllegalArgumentException("Both email and password must be provided");

    String email = EmailFormat.process(request.getEmail());

    User user = repository.findByEmail(email).orElseThrow(UserNotFoundException::new);

    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, request.getPassword(), user.getAuthorities())
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);

    JwtService.TokenPair tokenPair = jwtService.generateTokenPair(user.getId(), user.getUsername(), user.getRole());
    tokensService.saveTokenPair(tokenPair, user);

    long lastPickedVocabularyId = user.getLastPickedVocabulary() == null
            ? -1
            : user.getLastPickedVocabulary().getId();

    return new AuthenticationResponse(
            new UserDetailsResponse(
                    user.getEmail(),
                    user.getFullName(),
                    roomCodeGenerationFactory.generateRoomCode(user),
                    profileImageFormatter.format(user.getUserImage()),
                    lastPickedVocabularyId
            ),
            new TokenResponse(
                    tokenPair.refreshToken(),
                    tokenPair.accessToken()
            ),
            new SettingsResponse(
                    user.getUserSettings().getUserInterfaceLanguage(),
                    user.getUserSettings().getWordsPerPage()
            ),
            new StreakResponse(
                    UserStreakService.getStreak(user.getUserStreakStats()),
                    user.getUserStreakStats().getMaxStreak()
            )
    );
  }

  @Transactional
  public TokenResponse authenticateAdmin(AuthenticationRequest request) {
    if (Objects.isNull(request.getPassword()) || Objects.isNull(request.getEmail()))
      throw new IllegalStateException("null values");

    User user = repository.findByEmail(request.getEmail())
            .orElseThrow(UserNotFoundException::new);

    if (!user.getRole().equals(Role.ROLE_ADMIN)) {
      throw new IllegalArgumentException("User is not an admin");
    }

    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(),
                    request.getPassword(), user.getAuthorities()));

    tokensService.revokeAllUserTokens(user);
    JwtService.TokenPair tokenPair = jwtService.generateTokenPair(user.getId(), user.getEmail(), user.getRole());
    tokensService.saveTokenPair(tokenPair, user);

    SecurityContextHolder.getContext().setAuthentication(authentication);

    return new TokenResponse(
            tokenPair.refreshToken(),
            tokenPair.accessToken()
    );
  }

  @Transactional
  public TokenResponse refreshToken(String refreshToken) {
    if (refreshToken == null) throw new IllegalArgumentException("Refresh token mustn't be null");
    Optional<RefreshToken> token = refreshTokenRepository.findTopByRefreshToken(refreshToken);

    if (token.isEmpty()) {
      throw new IllegalArgumentException("Refresh token doesn't exist");
    } else if (token.get().getExpiresAt().isBefore(LocalDateTime.now())) {
      throw new IllegalArgumentException("Refresh token is expired");
    } else if (token.get().isRevoked()) {
      throw new IllegalArgumentException("Refresh token is revoked");
    }


    JwtService.TokenPair tokenPair = jwtService.refreshAccessToken(refreshToken);
    try {
      tokensService.saveAuthenticationToken(tokenPair.accessToken(), token.get());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return new TokenResponse(tokenPair.refreshToken(), tokenPair.accessToken());
  }

  private User fromRequest(RegisterRequest request) {
    var user = userMapper.mapToUser(request);

    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.ROLE_USER);
    user.setCreatedAt(LocalDateTime.now());
    user.setUserLevel(Difficulty.EASY);
    user.setUserStreakStats(UserStreakService.initialize());
    user.setUserSettings(UserSettingsService.initialize());
    user.setUserImage(UserImage.builder().imageType(ImageType.DEFAULT).build());
    user.setAccountCreationType(AccountCreationType.EMAIL);
    user.setConfirmed(true);

    return user;
  }

}
