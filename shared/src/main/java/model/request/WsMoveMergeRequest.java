package model.request;

import chess.ChessMove;
import websocket.commands.UserGameCommand;

public class WsMoveMergeRequest {

    private UserGameCommand.CommandType commandType;
    private String authToken;
    private  Integer gameID;
    private ChessMove move;


    public WsMoveMergeRequest(UserGameCommand userGameCommand, WsMoveRequest wsMoveRequest) {
        this.commandType = userGameCommand.getCommandType();
        this.move = wsMoveRequest.move();
        this.authToken = userGameCommand.getAuthToken();
        this.gameID = userGameCommand.getGameID();
    }

    public UserGameCommand.CommandType getUserGameCommand() {
        return commandType;
    }

    public ChessMove getWsMoveRequest() {
        return move;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }
}
