package com.qualitypaper.fluentfusion.service.user;

import com.qualitypaper.fluentfusion.exception.notfound.NotFoundException;
import com.qualitypaper.fluentfusion.exception.notfound.UserNotFoundException;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.repository.LearningSessionRepository;
import com.qualitypaper.fluentfusion.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDbService {

  private final UserRepository userRepository;
  private final LearningSessionRepository learningSessionRepository;

  @Transactional(readOnly = true)
  public List<LearningSessionRepository.RecentLearningSession> getRecentLearningSessions(User user, Duration interval) {
    return learningSessionRepository.findByUserAndCompletedAtAfter(user, LocalDateTime.now().minus(interval));
  }

  public User getUser(SecurityContext securityContext) throws NotFoundException {
    String principal = (String) securityContext.getAuthentication().getPrincipal();
    if (principal.equals("anonymousUser")) {
      return null;
    }

    return getUser(principal);
  }

  public User getUser(String username) throws UserNotFoundException {

    return userRepository.findByEmail(username)
            .orElseThrow(UserNotFoundException::new);
  }

  @Transactional
  public User save(User user) {
    return userRepository.save(user);
  }

  @Transactional(readOnly = true)
  public List<User> getActiveUsers() {
    return userRepository.findAllByLastActiveAtAfter(LocalDateTime.now().minus(Duration.ofDays(30)));
  }
}
