package com.qualitypaper.fluentfusion.config.auth;

import com.qualitypaper.fluentfusion.model.tokens.auth.AuthenticationToken;
import com.qualitypaper.fluentfusion.repository.AuthenticationTokenRepository;
import com.qualitypaper.fluentfusion.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final AuthenticationTokenRepository authenticationTokenRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  @Override
  public void logout(
          HttpServletRequest request,
          HttpServletResponse response,
          Authentication authentication
  ) {
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }
    jwt = authHeader.substring(6).replace(" ", "");
    AuthenticationToken storedToken = authenticationTokenRepository.findTopByToken(jwt)
            .orElse(null);

    if (storedToken == null) return;

    storedToken.setExpired(true);
    storedToken.setRevoked(true);
    authenticationTokenRepository.save(storedToken);

    storedToken.getRefreshToken().setRevoked(true);
    refreshTokenRepository.save(storedToken.getRefreshToken());
    SecurityContextHolder.clearContext();
  }
}
