package com.qualitypaper.fluentfusion.service.user.auth;

import com.qualitypaper.fluentfusion.model.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
  private final JwtEncoder jwtEncoder;
  private final JwtDecoder jwtDecoder;

  public static final Duration ACCESS_TOKEN_EXPIRATION = Duration.ofDays(1);
  public static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(365);

  public record TokenPair(String accessToken, String refreshToken) {
  }

  public String extractUsername(String token) throws BadJwtException{
    String subject = extractClaim(token, "sub");
    if (subject == null)
      throw new BadJwtException("Malformed jwt");

    return subject;
  }

  public <T> T extractClaim(String token, String claim) {
    final Map<String, Object> claims = extractAllClaims(token);
    if (claims.isEmpty()) return null;
    return (T) claims.get(claim);
  }

  public TokenPair generateTokenPair(long id, String username, Role role) {
    String accessToken = generateAccessToken(id, username, role);
    String refreshToken = generateRefreshToken(id, username);
    return new TokenPair(accessToken, refreshToken);
  }

  public TokenPair generateTokenPair(long id, String username) {
    return generateTokenPair(id, username, Role.ROLE_USER);
  }

  public String generateAccessToken(long id, String username, Role role) {
    Instant now = Instant.now();
    JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .issuer("pingo_family")
            .claim("authorities", List.of(role.name()))
            .subject(username)
            .claim("id", id)
            .claim("type", "access")
            .expiresAt(now.plus(ACCESS_TOKEN_EXPIRATION))
            .build();

    return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }

  private String generateRefreshToken(long id, String username) {
    Instant now = Instant.now();
    JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .issuer("pingo_family")
            .subject(username)
            .claim("id", id)
            .claim("type", "refresh")
            .expiresAt(now.plus(REFRESH_TOKEN_EXPIRATION))
            .build();

    return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }

  public String generateWrongToken() {
    JwtClaimsSet claimsSet = JwtClaimsSet.builder()
            .issuedAt(Instant.now())
            .issuer("fluent_fusion")
            .subject(TokensService.generateRandomString(20))
            .claim("id", 1234L)
            .claim("type", "access")
            .expiresAt(Instant.now().plus(1, ChronoUnit.SECONDS))
            .build();
    return this.jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    if (username == null || username.isEmpty()) return false;
    return (username.equals(userDetails.getUsername())) && isTokenValid(token);
  }

  public TokenPair refreshAccessToken(String refreshToken) {
    if (!isRefreshTokenValid(refreshToken)) {
      throw new JwtException("Invalid or expired refresh token");
    }

    String username = extractUsername(refreshToken);
    long userId = extractClaim(refreshToken, "id");

    return generateTokenPair(userId, username);
  }

  private boolean isRefreshTokenValid(String refreshToken) {
    try {
      Map<String, Object> claims = extractAllClaims(refreshToken);
      if (claims.isEmpty() || !"refresh".equals(claims.get("type"))) {
        return false;
      }
      return isTokenValid(refreshToken);
    } catch (JwtValidationException e) {
      return false;
    }
  }

  private boolean isTokenValid(String token) {
    Instant expiration = extractExpiration(token);
    return expiration == null || !expiration.isBefore(Instant.now());
  }

  private Instant extractExpiration(String token) {
    return extractClaim(token, "exp");
  }

  private Map<String, Object> extractAllClaims(String token) {
    try {
      return jwtDecoder.decode(token).getClaims();
    } catch (JwtValidationException e) {
      return new HashMap<>();
    }
  }
}
