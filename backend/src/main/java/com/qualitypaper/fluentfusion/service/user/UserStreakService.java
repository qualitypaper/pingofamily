package com.qualitypaper.fluentfusion.service.user;

import com.qualitypaper.fluentfusion.controller.dto.response.auth.StreakResponse;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.user.UserStreakStats;
import com.qualitypaper.fluentfusion.repository.UserRepository;
import com.qualitypaper.fluentfusion.util.DateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStreakService {


  private final UserRepository userRepository;

  public static StreakResponse toResponse(UserStreakStats userStreakStats) {
    return new StreakResponse(userStreakStats.getStreak(), userStreakStats.getMaxStreak());
  }

  public static TrainingStreakResult isStreak(Long lastTrainingTime) {
    if (lastTrainingTime == null || lastTrainingTime < 1) {
      return TrainingStreakResult.STREAK_RESET;
    }

    if (DateService.isToday(lastTrainingTime)) {
      return TrainingStreakResult.STREAK_NOT_UPDATE;
    } else if (DateService.isYesterday(lastTrainingTime)) {
      return TrainingStreakResult.STREAK_UPDATE;
    } else {
      return TrainingStreakResult.STREAK_RESET;
    }
//        }
  }

  public static long getStreak(Long currentStreak, Long lastTrainingTime) {
    TrainingStreakResult streak = isStreak(lastTrainingTime);

    return streak.equals(TrainingStreakResult.STREAK_RESET)
            ? 0
            : currentStreak;
  }

  public static long getStreak(UserStreakStats userStreakStats) {
    return getStreak(userStreakStats.getStreak(), userStreakStats.getLastTrainingTime());
  }

  public static UserStreakStats initialize() {
    return UserStreakStats.builder()
            .streak(0L)
            .maxStreak(0L)
            .lastTrainingTime(System.currentTimeMillis())
            .build();
  }

  public void updateUserStreak(User user) {
    UserStreakStats userStreakStats = user.getUserStreakStats();
    long streak = -1;

    switch (isStreak(userStreakStats.getLastTrainingTime())) {
      case STREAK_UPDATE -> streak = userStreakStats.getStreak();
      case STREAK_RESET -> streak = 0;
      case STREAK_NOT_UPDATE -> streak = userStreakStats.getStreak() - 1;
    }

    userStreakStats.setStreak(streak + 1);
    userStreakStats.setLastTrainingTime(System.currentTimeMillis());
    userStreakStats.setMaxStreak(Math.max(userStreakStats.getMaxStreak(), userStreakStats.getStreak()));
    userRepository.save(user);
  }

  public enum TrainingStreakResult {
    STREAK_UPDATE,
    STREAK_NOT_UPDATE,
    STREAK_RESET
  }
}
