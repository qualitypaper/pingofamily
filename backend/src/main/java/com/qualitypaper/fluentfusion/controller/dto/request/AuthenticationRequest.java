package com.qualitypaper.fluentfusion.controller.dto.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class AuthenticationRequest {
  private String email;
  @Getter
  private String password;

  public String getEmail() {
    return email.toLowerCase();
  }
}
