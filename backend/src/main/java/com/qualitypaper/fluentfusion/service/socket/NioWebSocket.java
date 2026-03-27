package com.qualitypaper.fluentfusion.service.socket;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class NioWebSocket extends WebSocketServer {

  private final Map<String, Map<String, WebSocket>> connections = new HashMap<>();

  private final Predicate<String> checkToken;

  public NioWebSocket(int port, Predicate<String> checkToken) {
    super(new InetSocketAddress(port));
    this.checkToken = checkToken;
  }

  @Override
  public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
    String data = webSocket.getResourceDescriptor();
    Matcher tokenMath = Pattern.compile("token=([^&\\r\\n ]+)").matcher(data);
    if (!tokenMath.find()) {
      webSocket.send("Authentication data wasn't found");
      webSocket.close();
    } else if (!checkToken.test(tokenMath.group(1).trim())) {
      log.warn("Couldn't establish connection with {}, token is invalid", webSocket.getRemoteSocketAddress().getAddress().getHostAddress());
      webSocket.close();
    }

    Matcher roomMatch = Pattern.compile("room=([^&]+)").matcher(data);

    if (!roomMatch.find()) {
      webSocket.send("Authentication data wasn't found (ROOM)");
      webSocket.close();
    }
    String room = roomMatch.group(1).trim();
    String token = tokenMath.group(1).trim();

    if (!connections.containsKey(room)) {
      connections.put(room, new HashMap<>());
    }
    connections.get(room).put(token, webSocket);
  }

  @Override
  public void onClose(WebSocket webSocket, int i, String s, boolean b) {
    webSocket.close();
  }

  @Override
  public void onMessage(WebSocket webSocket, String s) {
    throw new RuntimeException("One Side Websocket");
  }

  @Override
  public void onError(WebSocket webSocket, Exception e) {
    webSocket.send("""
             {
             "error": "%s"
             }
            """.formatted(e.getMessage()));
    webSocket.close();
    throw new RuntimeException(e);
  }

  @Override
  public void onStart() {
    log.info("Started websocket server on port: {}", this.getPort());
  }

  public void sendMessage(Object message, String room, SocketEventType socketEventType) {
    if (connections.containsKey(room)) {
      connections.get(room).forEach((_, v) -> {
        if (!v.isClosed())
          v.send(combineEventAndBody(message, socketEventType));
      });
      log.info("Sent message to room: {}", room);
    }
  }

  private String combineEventAndBody(Object message, SocketEventType socketEventType) {
    return """
            {
                "event": "%s",
                "data": %s
            }
            """.formatted(socketEventType.name(), new Gson().toJson(message));
  }
}
