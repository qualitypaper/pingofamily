package com.qualitypaper.fluentfusion.service.user;

import com.qualitypaper.fluentfusion.controller.dto.response.UserStatisticsResponse;
import com.qualitypaper.fluentfusion.controller.dto.response.auth.UserResponse;
import com.qualitypaper.fluentfusion.exception.notfound.UserNotFoundException;
import com.qualitypaper.fluentfusion.mappers.user.UserMapper;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.tokens.TokenType;
import com.qualitypaper.fluentfusion.model.user.*;
import com.qualitypaper.fluentfusion.model.vocabulary.Difficulty;
import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import com.qualitypaper.fluentfusion.repository.*;
import com.qualitypaper.fluentfusion.service.email.EmailService;
import com.qualitypaper.fluentfusion.service.email.NotificationService;
import com.qualitypaper.fluentfusion.service.pts.s3.ObjectType;
import com.qualitypaper.fluentfusion.service.pts.s3.S3Service;
import com.qualitypaper.fluentfusion.service.user.auth.TokensService;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabulary.VocabularyDbService;
import com.qualitypaper.fluentfusion.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
  private final UserImageRepository userImageRepository;

  public static final User WORD_SCRIPT_USER = User.builder()
          .id(-10L)
          .role(Role.ROLE_ADMIN)
          .userLevel(Difficulty.EASY)
          .accountCreationType(AccountCreationType.EMAIL)
          .email("word_adding@fluent.com")
          .userVocabularies(new ArrayList<>())
          .refreshTokens(new ArrayList<>())
          .password("test")
          .confirmed(true)
          .fullName("Script User")
          .build();

  private final S3Service s3Service;
  private final UserSettingsRepository userSettingsRepository;
  private final UserDbService userDbService;
  private final UserRepository userRepository;
  private final ConfirmationTokenRepository confirmationTokenRepository;
  private final TokensService tokensService;
  private final EmailService emailService;
  private final ForgotPasswordRepository forgotPasswordRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserSettingsService userSettingsService;
  private final UserMapper userMapper;
  private final VocabularyDbService vocabularyDbService;
  private final NotificationService notificationService;

  @Value("${spring.web.resources.static-locations}")
  private String staticLocation;
  @Value(value = "${url.confirmation}")
  private String confirmationUrl;
  @Value(value = "${url.frontend-forgot-password}")
  private String forgotPasswordUrl;

  @EventListener(ApplicationReadyEvent.class)
  @Transactional
  public void init() {
    List<User> all = userRepository.findAll();
    for (User user : all) {
      if (user.getUserSettings() == null) {
        UserSettings userSettings = UserSettingsService.initialize();
        userSettings.setUser(user);
        userSettingsRepository.save(userSettings);
        user.setUserSettings(userSettings);
      }
      if (user.getUserStreakStats() == null) {
        UserStreakStats userStreakStats = UserStreakService.initialize();
        user.setUserStreakStats(userStreakStats);
      }

      if (user.getUserImage() == null) {
        UserImage userImage = UserImage.builder()
                .imageType(ImageType.DEFAULT)
                .build();
        userImageRepository.save(userImage);
        user.setUserImage(userImage);
      }

      userRepository.save(user);
    }
  }

  @Transactional
  public void confirmUser(String token) {
    var optional = confirmationTokenRepository.findByConfirmationToken(token);
    if (optional.isEmpty()) throw new IllegalStateException("Token not found");
    var confirmationToken = optional.get();
    if (!tokensService.checkConfirmationTokenExpiration(confirmationToken))
      throw new IllegalStateException("Token expired");

    var user = confirmationToken.getUser();
    user.setConfirmed(true);
    confirmationToken.setConfirmedAt(LocalDateTime.now());
    confirmationTokenRepository.save(confirmationToken);
    userRepository.save(user);
  }

  @Transactional
  public void forgotPassword(String token, String newPassword) {
    var forgotPasswordToken = forgotPasswordRepository.findForgotPasswordByToken(token);
    if (!tokensService.checkForgotPasswordTokenExpiration(forgotPasswordToken))
      throw new IllegalStateException("Token expired");

    var user = forgotPasswordToken.getUser();
    user.setPassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
    tokensService.revokeAllUserTokens(user);
  }

  @Transactional
  public String generateNewTokenAndSendEmail(TokenType type, String email) {
    switch (type) {
      case TokenType.CONFIRMATION -> {
        String confirmationToken;
        try {
          confirmationToken = getNewConfirmationToken(email);
        } catch (IllegalStateException _) {
          return "User already confirmed";
        }

        notificationService.sendVerificationEmail(email, confirmationUrl + confirmationToken);

        return confirmationToken;
      }
      case TokenType.FORGOT_PASSWORD -> {
        String forgotPasswordToken = getNewForgotPasswordToken(email);
        notificationService.sendPasswordResetEmail(email, forgotPasswordUrl + forgotPasswordToken);

        return forgotPasswordToken;
      }
      default -> throw new IllegalStateException("Invalid token type");
    }
  }

  public void changeName(String newName) {
    User user = userDbService.getUser(SecurityContextHolder.getContext());

    user.setFullName(newName);
    userRepository.save(user);
  }

  private String getNewConfirmationToken(String userEmail) {
    var optional = userRepository.findByEmail(userEmail);
    if (optional.isEmpty()) throw new UserNotFoundException();

    var user = optional.get();
    if (Boolean.TRUE.equals(user.getConfirmed())) throw new IllegalStateException("User already confirmed");

    var confirmationToken = TokensService.generateRandomString(30);
    tokensService.saveUserConfirmationToken(user, confirmationToken);
    return confirmationToken;
  }

  private String getNewForgotPasswordToken(String userEmail) {
    var optional = userRepository.findByEmail(userEmail);
    if (optional.isEmpty()) throw new UserNotFoundException();

    var user = optional.get();
    var forgotPasswordToken = TokensService.generateRandomString(30);
    tokensService.saveUserForgotPasswordToken(user, forgotPasswordToken);

    return forgotPasswordToken;
  }

  @Transactional
  public void changeInterfaceLanguage(Language language) {
    User user = userDbService.getUser(SecurityContextHolder.getContext());

    user.getUserSettings().setUserInterfaceLanguage(language);
    userSettingsService.save(user.getUserSettings());
  }

  @Transactional
  public void updateLastPickedVocabulary(Long vocabularyId) {
    User user = userDbService.getUser(SecurityContextHolder.getContext());
    Vocabulary vocabulary = vocabularyDbService.findById(vocabularyId);

    user.setLastPickedVocabulary(vocabulary);
    userRepository.save(user);
  }

  @Transactional
  public void changeAvatar(MultipartFile file) {
    User user = userDbService.getUser(SecurityContextHolder.getContext());
    Path to = Path.of(staticLocation);
    String filename = FileUtils.saveFromMultiPartFile(file, user, to);
    String url = s3Service.uploadObject(filename, ObjectType.IMAGE);

    UserImage userImage = UserImage.builder()
            .imageUrl(url)
            .imageType(ImageType.CUSTOM)
            .build();
    userImageRepository.save(userImage);

    user.setUserImage(userImage);
    userRepository.save(user);
  }

  @Transactional
  public UserResponse getUserDetails() {
    try {
      User user = userDbService.getUser(SecurityContextHolder.getContext());
      UserStreakStats userStreakStats = user.getUserStreakStats();
      long streak = UserStreakService.getStreak(userStreakStats);

      userStreakStats.setMaxStreak(Math.max(userStreakStats.getStreak(), userStreakStats.getMaxStreak()));
      userStreakStats.setStreak(streak);
      userRepository.save(user);

      return userMapper.fromUser(user);
    } catch (NullPointerException e) {
      log.error(e.getMessage());
      return null;
    }
  }

  @Transactional(readOnly = true)
  public List<UserStatisticsResponse> getUserStatistics(int days) {
    User user = userDbService.getUser(SecurityContextHolder.getContext());

    List<LearningSessionRepository.RecentLearningSession> recentLearningSessions = userDbService.getRecentLearningSessions(user, Duration.ofDays(days));
    return recentLearningSessions.stream()
            .map(e -> new UserStatisticsResponse(e.getCompletedAt(), e.getVocabularyId()))
            .toList();
  }
}
