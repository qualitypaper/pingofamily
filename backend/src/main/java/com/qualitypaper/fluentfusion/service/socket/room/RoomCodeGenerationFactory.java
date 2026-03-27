package com.qualitypaper.fluentfusion.service.socket.room;

import com.qualitypaper.fluentfusion.model.user.User;

public interface RoomCodeGenerationFactory {

  String generateRoomCode(User user);
}
