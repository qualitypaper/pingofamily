package com.qualitypaper.fluentfusion.mappers.user;

import com.qualitypaper.fluentfusion.controller.dto.request.RegisterRequest;
import com.qualitypaper.fluentfusion.controller.dto.response.auth.UserAdminResponse;
import com.qualitypaper.fluentfusion.controller.dto.response.auth.UserResponse;
import com.qualitypaper.fluentfusion.model.user.User;

public interface UserMapper {

  User mapToUser(RegisterRequest request);

  UserResponse fromUser(User e);
  UserAdminResponse from(User user);
}
