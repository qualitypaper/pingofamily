package com.qualitypaper.fluentfusion.service.socket.room;


import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.util.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class DefaultUserRoomCodeGeneration implements RoomCodeGenerationFactory {
  @Override
  public String generateRoomCode(User user) {
    return StringUtils.encodeMD5(user.getEmail());
  }
}
