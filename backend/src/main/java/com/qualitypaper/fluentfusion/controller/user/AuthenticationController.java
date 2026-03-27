package com.qualitypaper.fluentfusion.controller.user;

import com.qualitypaper.fluentfusion.controller.dto.request.AuthenticationRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.FormBodyRequest;
import com.qualitypaper.fluentfusion.controller.dto.request.RegisterRequest;
import com.qualitypaper.fluentfusion.controller.dto.response.auth.AuthenticationResponse;
import com.qualitypaper.fluentfusion.controller.dto.response.auth.TokenResponse;
import com.qualitypaper.fluentfusion.service.email.FormResendService;
import com.qualitypaper.fluentfusion.service.typealiase.MapSB;
import com.qualitypaper.fluentfusion.service.user.UserService;
import com.qualitypaper.fluentfusion.service.user.auth.AuthenticationService;
import com.qualitypaper.fluentfusion.util.HttpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;
  private final FormResendService formResendService;
  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
    return ResponseEntity.ok(service.register(request));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/authenticate-admin")
  public ResponseEntity<TokenResponse> authenticateAdmin(@RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(service.authenticateAdmin(request));
  }

  @PutMapping("/refresh")
  public ResponseEntity<TokenResponse> updateToken(@RequestBody UserController.RefreshTokenRequest request) {
    return ResponseEntity.ok(service.refreshToken(request.refreshToken()));
  }

  @GetMapping("/confirm-user/{token}")
  public ResponseEntity<String> confirmUser(@PathVariable String token) {
    userService.confirmUser(token);
    return ResponseEntity.ok("User confirmed");
  }

  @GetMapping("/forgot-password/{token}")
  public ResponseEntity<String> forgotPassword(@PathVariable String token, @RequestParam String newPassword) {
    userService.forgotPassword(token, newPassword);
    return ResponseEntity.ok("Password changed");
  }

  @PostMapping("/contact-us")
  public ResponseEntity<MapSB> sendBotMessage(@RequestBody FormBodyRequest formBodyRequest) {
    formResendService.sendForm(formBodyRequest);
    return ResponseEntity.ok(HttpUtils.successResponse());
  }
}
