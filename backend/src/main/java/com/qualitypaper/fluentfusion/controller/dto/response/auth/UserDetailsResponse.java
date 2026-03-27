package com.qualitypaper.fluentfusion.controller.dto.response.auth;

public record UserDetailsResponse(
        String email,
        String name,
        String roomCode,
        String profileImageUrl,
        Long lastPickedVocabularyId
) {
}
