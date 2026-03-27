package com.qualitypaper.fluentfusion.controller.user;

import com.qualitypaper.fluentfusion.controller.dto.response.UserStatisticsResponse;
import com.qualitypaper.fluentfusion.controller.dto.response.auth.UserResponse;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.tokens.TokenType;
import com.qualitypaper.fluentfusion.service.typealiase.MapSB;
import com.qualitypaper.fluentfusion.service.user.UserService;
import com.qualitypaper.fluentfusion.util.HttpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

  private final UserService userService;


  @GetMapping("/get-confirmation-token")
  public String getNewConfirmationToken(@RequestHeader("Authorization") String token) {
    return userService.generateNewTokenAndSendEmail(TokenType.CONFIRMATION, token);
  }

  @GetMapping("/get-forget-password-token")
  public String getNewForgotPasswordToken(@RequestParam String email) {
    return userService.generateNewTokenAndSendEmail(TokenType.FORGOT_PASSWORD, email);
  }

  @PostMapping("/change-name")
  public MapSB updateName(@RequestBody ChangeStringParameter changeStringParameter) {
    userService.changeName(changeStringParameter.data());
    return HttpUtils.successResponse();
  }

  @GetMapping("/update-last-picked-vocabulary/{id}")
  public MapSB updateLastPickedVocabulary(@PathVariable Long id) {
    userService.updateLastPickedVocabulary(id);
    return HttpUtils.successResponse();
  }

  @GetMapping("/change-interface-language")
  public MapSB changeInterfaceLanguage(@RequestParam String language) {
    userService.changeInterfaceLanguage(Language.valueOf(language.toUpperCase()));
    return HttpUtils.successResponse();
  }

  @GetMapping("/get-user-details")
  @ExceptionHandler(value = HttpClientErrorException.Unauthorized.class)
  public ResponseEntity<UserResponse> getUserDetails() {
    return ResponseEntity.ok(userService.getUserDetails());
  }

  @PostMapping("/change-avatar")
  public MapSB changeAvatar(@RequestParam("file") MultipartFile file) {
    userService.changeAvatar(file);
    return HttpUtils.successResponse();
  }

  @GetMapping("/get-statistics")
  public ResponseEntity<List<UserStatisticsResponse>> getStatistics(@RequestParam int days) {
    return ResponseEntity.ok(userService.getUserStatistics(days));
  }

  public record ChangeStringParameter(String data) {
  }

  public record RefreshTokenRequest(String refreshToken) {}
}
