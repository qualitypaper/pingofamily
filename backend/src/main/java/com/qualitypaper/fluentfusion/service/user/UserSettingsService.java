package com.qualitypaper.fluentfusion.service.user;

import com.qualitypaper.fluentfusion.controller.dto.response.auth.SettingsResponse;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.user.UserSettings;
import com.qualitypaper.fluentfusion.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSettingsService {

  private final UserSettingsRepository userSettingsRepository;

  public static SettingsResponse toResponse(UserSettings userSettings) {
    return new SettingsResponse(userSettings.getUserInterfaceLanguage(), userSettings.getWordsPerPage());
  }

  public UserSettings save(UserSettings userSettings) {
    return userSettingsRepository.save(userSettings);
  }

  public static UserSettings initialize() {
    return UserSettings.builder()
            .userInterfaceLanguage(Language.ENGLISH)
            .wordsPerPage(50)
            .build();
  }

}
