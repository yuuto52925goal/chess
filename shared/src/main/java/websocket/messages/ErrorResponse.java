package websocket.messages;

public record ErrorResponse(ServerMessage.ServerMessageType serverMessageType, String errorMessage) {
}
