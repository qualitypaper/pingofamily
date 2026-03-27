package com.qualitypaper.fluentfusion.controller.dto.response.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public record UserAdminResponse(long id, long vocabulariesCount, UserDetailsResponse userDetails,
                                StreakResponse streak, SettingsResponse settings,
                                @JsonSerialize(using = LocalDateTimeSerializer.class)
                                @JsonFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
                                LocalDateTime createdAt) {
}
