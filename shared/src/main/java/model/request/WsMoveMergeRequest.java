package model.request;

import websocket.commands.UserGameCommand;

public class WsMoveMergeRequest {

    UserGameCommand userGameCommand;
    WsMoveRequest wsMoveRequest;

    public WsMoveMergeRequest(UserGameCommand userGameCommand, WsMoveRequest wsMoveRequest) {
        this.userGameCommand = userGameCommand;
        this.wsMoveRequest = wsMoveRequest;
    }

    public UserGameCommand getUserGameCommand() {
        return userGameCommand;
    }

    public WsMoveRequest getWsMoveRequest() {
        return wsMoveRequest;
    }
}
