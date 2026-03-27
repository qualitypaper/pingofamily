package com.qualitypaper.fluentfusion.config.auth;

import com.qualitypaper.fluentfusion.config.SecurityConfiguration;
import com.qualitypaper.fluentfusion.model.tokens.auth.AuthenticationToken;
import com.qualitypaper.fluentfusion.repository.AuthenticationTokenRepository;
import com.qualitypaper.fluentfusion.service.user.UserDbService;
import com.qualitypaper.fluentfusion.service.user.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationTokenRepository authenticationTokenRepository;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final UserDbService userDbService;


    /**
     * @return true, when an endpoint is public. false otherwise
     */
    @Override
    protected boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        AntPathMatcher pathMatcher = new AntPathMatcher();

        return Stream.of(SecurityConfiguration.ENDPOINT_WHITE_LIST)
                .anyMatch(e -> pathMatcher.match(e, request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No auth data presented for request to {}", request.getServletPath());
            authenticationEntryPoint.commence(request, response, new BadCredentialsException("No auth data present"));
            return;
        }

        jwt = authHeader.substring(6).replace(" ", "");
        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (BadJwtException _) {
            authenticationEntryPoint.commence(request, response, new BadCredentialsException("Malformed JWT"));
            return;
        }

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
        Optional<AuthenticationToken> authToken = authenticationTokenRepository.findTopByToken(jwt);

        if (authToken.isEmpty()) {
            // If the token is not found in the database, we do not authenticate the user
            log.warn("Authentication token not found in the database for user: {}", userEmail);
            authenticationEntryPoint.commence(request, response, new BadCredentialsException("Auth token isn't present in the database"));
            return;
        }

        boolean isTokenValid = authToken
                .map(t -> !t.isExpired() && !t.isRevoked())
                .orElse(false);

        if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
            UsernamePasswordAuthenticationToken upaToken = new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );
            upaToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(upaToken);

            authToken.get().getRefreshToken().getUser().setLastActiveAt(LocalDateTime.now());
            userDbService.save(authToken.get().getRefreshToken().getUser());

            authToken.get().setLastLogin(LocalDateTime.now());
            authenticationTokenRepository.save(authToken.get());
        } else {
            authenticationEntryPoint.commence(request, response, new BadCredentialsException("Invalid token"));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
