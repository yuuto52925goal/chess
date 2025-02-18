package model.request;

public record JoinGameRequest(String token, String gameId, String playerColor) {
}
