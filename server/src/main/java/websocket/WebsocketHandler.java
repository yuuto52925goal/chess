package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSocket
public class WebsocketHandler {

    private final ConnectionManager connectionManager = new ConnectionManager();
    private static final Logger logger = LoggerFactory.getLogger(WebsocketHandler.class);

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        var action = new Gson().fromJson(message, UserGameCommand.class);
        switch (action.getCommandType()) {
            case CONNECT -> connect(action, session);
            case MAKE_MOVE -> makeMove(action, session);
            case LEAVE -> leaveGame(session);
            case RESIGN -> resignGame(session);
        }
    }

    public void connect(UserGameCommand command, Session session) {
//        connectionManager.add(name, session);
//        System.out.println("Connected to " + name);
//        logger.info("Connected to {}", name);
//        var message = String.format("%s connected", name);
//        var notification = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId);

    }

    public void makeMove(UserGameCommand command, Session session) {

    }

    public void leaveGame(Session session) {

    }

    public void resignGame(Session session) {
        System.out.println("Resigned game");
    }

}
