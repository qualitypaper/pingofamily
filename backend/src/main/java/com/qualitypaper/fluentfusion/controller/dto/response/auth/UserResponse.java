package com.qualitypaper.fluentfusion.controller.dto.response.auth;

public record UserResponse(UserDetailsResponse userDetails, SettingsResponse settings,
                           StreakResponse streak) {
}
