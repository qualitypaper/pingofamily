package com.qualitypaper.fluentfusion.config;

import com.qualitypaper.fluentfusion.config.auth.JwtAuthenticationFilter;
import com.qualitypaper.fluentfusion.controller.dto.response.auth.AuthenticationResponse;
import com.qualitypaper.fluentfusion.service.user.Oauth2AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final LogoutHandler logoutHandler;
    private final Oauth2AuthenticationService oauth2AuthenticationService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public static final String[] ENDPOINT_WHITE_LIST = { "/static/**",
            "/auth/**",
            "/login/**",
            "/error/**",
            "/login/oauth2/**",
            "/admin/user/recall",
            "/user/get-forget-password-token",
            "/user-vocabulary/get-words",
            "/user-vocabulary/get-words/*",
            "/user-vocabulary/get-word",
            "/vocabulary-group/get-all-suggested",
            "/vocabulary-group/get-suggested",
            "/learning-test/**"
    };

    @Value("${microservice.frontend.host}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(request -> request
                        .requestMatchers(ENDPOINT_WHITE_LIST)
                        .permitAll()
                        .requestMatchers(
                                "/scripts/**",
                                "/admin/**")
                        .hasRole("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(logout -> logout.logoutUrl("/auth/logout").addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((_, _, _) -> SecurityContextHolder.clearContext()))
                .oauth2Login(oauth -> oauth
                        .loginPage("/login/oauth2")
                        .redirectionEndpoint(r -> r.baseUri("/login/oauth2/code/**"))
                        .authorizationEndpoint(auth -> auth.baseUri("/login/oauth2/authorization"))
                        .successHandler((_, response, authentication) -> {
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                            AuthenticationResponse authenticationResponse = oauth2AuthenticationService
                                    .authenticateOrCreateOauth2User(oAuth2User);

                            String url = buildUrl(authenticationResponse);

                            response.sendRedirect(url);
                        }))
                .formLogin(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @NotNull
    private String buildUrl(AuthenticationResponse authenticationResponse) {
        String lastPickedVocabularyId = authenticationResponse.userDetails().lastPickedVocabularyId() == null
                ? ""
                : String.valueOf(authenticationResponse.userDetails().lastPickedVocabularyId());

        return frontendUrl + "/" + authenticationResponse.settings().interfaceLanguage().getCollapsed() +
                "/auth/" + lastPickedVocabularyId + "?accessToken=" + authenticationResponse.tokens().accessToken() +
                "&refreshToken=" + authenticationResponse.tokens().refreshToken();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(
                List.of(
                        frontendUrl,
                        "http://frontend"));
        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization", "Content-Type"));
        corsConfiguration.setMaxAge(3600L);
        corsConfiguration.applyPermitDefaultValues();

        return _ -> corsConfiguration;
    }

}
