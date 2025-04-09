package websocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import model.data.ConnectionData;
import model.request.WsConnectRequest;
import model.request.WsMoveRequest;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {

    private final Logger logger = LoggerFactory.getLogger(WebsocketHandler.class);
    private final ConnectionManager connectionManager = new ConnectionManager();
    private final WsService wsService = new WsService(connectionManager);

    @OnWebSocketConnect
    public void onConnect(Session session) {
        logger.info("onConnect");
    }
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        logger.info("onClose");
    }



    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        var action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> connect(action, session, message);
            case MAKE_MOVE -> makeMove(action, session, message);
            case LEAVE -> leaveGame(action, session);
            case RESIGN -> resignGame(action, session);
        }
    }

    public void connect(UserGameCommand command, Session session, String message) {
        try {
            logger.info("Connected to " + session.getRemoteAddress());
            wsService.joinGame(command, session);
        }catch (IOException e){
            logger.error(e.getMessage());
        }
    }

    public void makeMove(UserGameCommand command, Session session, String message){
        try {
            logger.info("Make move " + message);
            logger.info(command.toString());
            WsMoveRequest moveRequest = new Gson().fromJson(message, WsMoveRequest.class);
            wsService.makeMove(command, session, moveRequest);
        }catch (IOException | InvalidMoveException e){
            logger.error(e.getMessage());
        }
    }

    public void leaveGame(UserGameCommand userGameCommand, Session session) {
        try {
            logger.info("Leave game " + userGameCommand.toString());
            wsService.leaveGame(userGameCommand, session);
        }catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void resignGame(UserGameCommand userGameCommand, Session session) {
        try {
            logger.info("Resign game");
            wsService.resign(userGameCommand, session);
        } catch (IOException e){
            logger.error(e.getMessage());
        }
    }

}
