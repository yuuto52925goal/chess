package websocket.messages;

public record NotifiResponse(ServerMessage.ServerMessageType serverMessageType, String message) {
}
