package com.qualitypaper.fluentfusion.service.socket;

import com.qualitypaper.fluentfusion.model.tokens.auth.AuthenticationToken;
import com.qualitypaper.fluentfusion.repository.AuthenticationTokenRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocketService {

  private final AuthenticationTokenRepository authenticationTokenRepository;

  private NioWebSocket server;

  @Value("${microservice.websocket.port}")
  private int websocketPort;

  @PostConstruct
  public void init() {
    Predicate<String> predicate = token -> {
      Optional<AuthenticationToken> auth = authenticationTokenRepository.findTopByToken(token);
      return auth.isPresent()
              && !auth.get().isExpired()
              && auth.get().getRefreshToken() != null
              && !auth.get().getRefreshToken().getExpiresAt().isBefore(LocalDateTime.now());
    };

    server = new NioWebSocket(websocketPort, predicate);
    CompletableFuture.runAsync(() -> server.run());
  }

  @PreDestroy
  public void predestroy() throws InterruptedException {
    server.stop();
  }

  @Async
  public void sendMessage(String room, Object message, SocketEventType socketEventType) {
    log.info("A message was sent to room: {} with event type: {} \nContent: {}", room, socketEventType, message.toString());

    server.sendMessage(message, room, socketEventType);
  }

}