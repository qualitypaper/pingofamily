package com.qualitypaper.fluentfusion.mappers.user;

import com.qualitypaper.fluentfusion.controller.dto.request.RegisterRequest;
import com.qualitypaper.fluentfusion.controller.dto.response.auth.UserAdminResponse;
import com.qualitypaper.fluentfusion.controller.dto.response.auth.UserDetailsResponse;
import com.qualitypaper.fluentfusion.controller.dto.response.auth.UserResponse;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.service.email.EmailFormat;
import com.qualitypaper.fluentfusion.service.socket.room.RoomCodeGenerationFactory;
import com.qualitypaper.fluentfusion.service.user.ProfileImageFormatter;
import com.qualitypaper.fluentfusion.service.user.UserSettingsService;
import com.qualitypaper.fluentfusion.service.user.UserStreakService;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImplementation implements UserMapper {


  private final RoomCodeGenerationFactory roomCodeGenerationFactory;
  private final ProfileImageFormatter profileImageFormatter;

  public UserMapperImplementation(RoomCodeGenerationFactory roomCodeGenerationFactory, ProfileImageFormatter profileImageFormatter) {
    this.roomCodeGenerationFactory = roomCodeGenerationFactory;
    this.profileImageFormatter = profileImageFormatter;
  }

  @Override
  public User mapToUser(RegisterRequest request) {
    return User.builder()
            .fullName(request.getFullName())
            .email(EmailFormat.process(request.getEmail()))
            .password(request.getPassword())
            .build();
  }

  @Override
  public UserResponse fromUser(User user) {

    return new UserResponse(
            new UserDetailsResponse(
                    user.getEmail(),
                    user.getFullName(),
                    roomCodeGenerationFactory.generateRoomCode(user),
                    profileImageFormatter.format(user.getUserImage()),
                    user.getLastPickedVocabulary() == null
                            ? 0
                            : user.getLastPickedVocabulary().getId()
            ),
            UserSettingsService.toResponse(user.getUserSettings()),
            UserStreakService.toResponse(user.getUserStreakStats()));
  }

  @Override
  public UserAdminResponse from(User user) {

    return new UserAdminResponse(
            user.getId(),
            user.getUserVocabularies().size(),
            new UserDetailsResponse(
                    user.getEmail(),
                    user.getFullName(),
                    roomCodeGenerationFactory.generateRoomCode(user),
                    profileImageFormatter.format(user.getUserImage()),
                    user.getLastPickedVocabulary() == null
                            ? 0
                            : user.getLastPickedVocabulary().getId()
            ),
            UserStreakService.toResponse(user.getUserStreakStats()),
            UserSettingsService.toResponse(user.getUserSettings()),
            user.getCreatedAt()
    );

  }

}
