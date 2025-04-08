package server;

import com.google.gson.Gson;
//import com.sun.nio.sctp.NotificationHandler;
import model.request.WsMoveMergeRequest;
import model.request.WsMoveRequest;
import org.glassfish.tyrus.core.wsadl.model.Endpoint;
//import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadResponse;
import websocket.messages.NotifiResponse;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class WebSocketFacade extends Endpoint {

    Session session;

    public WebSocketFacade(String url) throws MalformedURLException {
        try {
            url = url.replace("http", "ws");
            URL socketUrl = new URL(url);

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketUrl.toURI());

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage.ServerMessageType notifiResponse = new Gson().fromJson(message, ServerMessage.ServerMessageType.class);
                    if (notifiResponse != null) {
                        switch (notifiResponse) {
                            case LOAD_GAME -> loadGame(message);
                            case NOTIFICATION -> notifyMessage(message);
                        }
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadGame(String message) {
        LoadResponse loadResponse = new Gson().fromJson(message, LoadResponse.class);
        System.out.println(loadResponse.game().game().getBoard());
    }

    public void notifyMessage(String message) {
        NotifiResponse notifiResponse = new Gson().fromJson(message, NotifiResponse.class);
        System.out.println(notifiResponse.message());
    }

    public void runUserCommand(String message) {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
            if (command.getCommandType().equals(UserGameCommand.CommandType.LEAVE)){
                this.session.close();
            }
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void makeMove(String message) {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            WsMoveRequest move = new Gson().fromJson(message, WsMoveRequest.class);
            WsMoveMergeRequest merge = new WsMoveMergeRequest(command, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(merge));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
