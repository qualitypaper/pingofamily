package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.tokens.auth.RefreshToken;
import com.qualitypaper.fluentfusion.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findTopByRefreshToken(String refreshToken);

  List<RefreshToken> findAllByUser(User user);
}