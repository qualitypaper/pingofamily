package com.qualitypaper.fluentfusion.controller.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AuthenticationResponse(
        UserDetailsResponse userDetails,
        TokenResponse tokens,
        SettingsResponse settings,
        StreakResponse streak
) {

}
