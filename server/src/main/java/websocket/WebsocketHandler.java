package websocket;

import com.google.gson.Gson;
import model.data.ConnectionData;
import model.request.WsConnectRequest;
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

    private final ConnectionManager connectionManager = new ConnectionManager();
    private static final Logger logger = LoggerFactory.getLogger(WebsocketHandler.class);

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        var action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> connect(action, session, message);
            case MAKE_MOVE -> makeMove(action, session);
            case LEAVE -> leaveGame(session);
            case RESIGN -> resignGame(session);
        }
    }

    public void connect(UserGameCommand command, Session session, String message)  {
        try {
            WsConnectRequest wsConnectRequest = new Gson().fromJson(message, WsConnectRequest.class);
            connectionManager.add(new ConnectionData(command.getAuthToken(), command.getGameID()), session);
            logger.info("Connected to " + session.getRemoteAddress());

            String notification = String.format("%s has joined the game", session.getRemoteAddress());
            connectionManager.broadcast(command.getAuthToken(), command.getGameID(), ServerMessage.ServerMessageType.LOAD_GAME, notification);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    public void makeMove(UserGameCommand command, Session session) {

    }

    public void leaveGame(Session session) {

    }

    public void resignGame(Session session) {
        System.out.println("Resigned game");
    }

}
