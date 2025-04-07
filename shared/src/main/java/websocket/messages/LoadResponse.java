package websocket.messages;

import chess.ChessGame;
import model.data.GameData;

public record LoadResponse (ServerMessage.ServerMessageType serverMessageType, GameData game) {
}
