package com.qualitypaper.fluentfusion.service.user;

import com.qualitypaper.fluentfusion.controller.dto.response.auth.*;
import com.qualitypaper.fluentfusion.model.tokens.auth.AuthenticationToken;
import com.qualitypaper.fluentfusion.model.tokens.auth.RefreshToken;
import com.qualitypaper.fluentfusion.model.user.*;
import com.qualitypaper.fluentfusion.model.vocabulary.Difficulty;
import com.qualitypaper.fluentfusion.repository.UserImageRepository;
import com.qualitypaper.fluentfusion.repository.UserRepository;
import com.qualitypaper.fluentfusion.repository.UserSettingsRepository;
import com.qualitypaper.fluentfusion.service.email.FormResendService;
import com.qualitypaper.fluentfusion.service.email.NotificationService;
import com.qualitypaper.fluentfusion.service.socket.room.RoomCodeGenerationFactory;
import com.qualitypaper.fluentfusion.service.user.auth.JwtService;
import com.qualitypaper.fluentfusion.service.user.auth.TokensService;
import com.qualitypaper.fluentfusion.util.types.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class Oauth2AuthenticationService {
  private final UserSettingsRepository userSettingsRepository;
  private final UserImageRepository userImageRepository;
  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final TokensService tokensService;
  private final FormResendService formResendService;
  private final RoomCodeGenerationFactory roomCodeGenerationFactory;
  private final ProfileImageFormatter profileImageFormatter;
  private final NotificationService notificationService;

  @Transactional
  public AuthenticationResponse authenticateOrCreateOauth2User(OAuth2User oAuth2User) {
    String userPassword = TokensService.generateRandomString(10);

    User user = repository.findByEmail(oAuth2User.getAttribute("email")).orElseGet(() -> {
      UserImage userImage = UserImage.builder()
              .imageUrl(oAuth2User.getAttribute("imageUrl"))
              .imageType(ImageType.SOCIAL_MEDIA)
              .build();
      userImageRepository.save(userImage);

      UserSettings settings = UserSettingsService.initialize();
      userSettingsRepository.save(settings);

      var newUser = User.builder()
              .email(oAuth2User.getAttribute("email"))
              .fullName(oAuth2User.getAttribute("name"))
              .password(passwordEncoder.encode(userPassword))
              .userImage(userImage)
              .role(Role.ROLE_USER)
              .accountCreationType(AccountCreationType.GOOGLE)
              .userVocabularies(new ArrayList<>())
              .userStreakStats(UserStreakService.initialize())
              .userSettings(settings)
              .refreshTokens(new ArrayList<>())
              .userLevel(Difficulty.EASY)
              .confirmed(true)
              .createdAt(LocalDateTime.now())
              .build();

      formResendService.sendInfoMessage("New user registered with Google: " + newUser.getEmail());
      notificationService.sendWelcomeEmail(newUser.getEmail(), newUser.getFullName());

      return repository.save(newUser);
    });

    JwtService.TokenPair tokenPair = jwtService.generateTokenPair(user.getId(), user.getUsername());
    Pair<RefreshToken, AuthenticationToken> _ = tokensService.saveTokenPair(tokenPair, user);

    Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

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
            new SettingsResponse(user.getUserSettings().getUserInterfaceLanguage(),
                    user.getUserSettings().getWordsPerPage()
            ),
            new StreakResponse(user.getUserStreakStats().getStreak(),
                    user.getUserStreakStats().getMaxStreak())
    );
  }
}
