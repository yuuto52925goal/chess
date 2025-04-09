package client.ws;

import chess.ChessGame;
import com.google.gson.Gson;
import model.request.WsMoveMergeRequest;
import model.request.WsMoveRequest;
import websocket.messages.ErrorResponse;
import ui.ChessBoardDrawer;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadResponse;
import websocket.messages.NotifiResponse;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    Session session;
    ChessBoardDrawer chessBoardDrawer;
    String userColor;
    private ChessGame chessGame;

    public WebSocketFacade(String url, String useColor) {
        try {
            this.userColor = useColor;
            url = url.replace("http", "ws");
            URI socketUrl = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketUrl);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
//                    System.out.println("You received " + message);
                    ServerMessage notifiResponse = new Gson().fromJson(message, ServerMessage.class);
                    if (notifiResponse.getServerMessageType() != null) {
                        switch (notifiResponse.getServerMessageType()) {
                            case LOAD_GAME -> loadGame(message);
                            case NOTIFICATION -> notifyMessage(message);
                            case ERROR -> errorShow(message);
                        }
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException e) {
            System.out.println("Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
    }

    public void loadGame(String message) {
        LoadResponse loadResponse = new Gson().fromJson(message, LoadResponse.class);
        ChessBoardDrawer.drawChessBoard(loadResponse.game().game().getBoard(), userColor, null);
        this.chessGame = loadResponse.game().game();
    }

    public void notifyMessage(String message) {
        NotifiResponse notifiResponse = new Gson().fromJson(message, NotifiResponse.class);
        System.out.println("Notification: " + notifiResponse.message());
    }

    public void errorShow(String message) {
        ErrorResponse errorResponse = new Gson().fromJson(message, ErrorResponse.class);
        if (errorResponse.errorMessage() == null){
            System.out.println("Error" + message);
        }else{
            System.out.println("Error triggered " + errorResponse.errorMessage());
        }
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

    public void makeChessMove(String message) {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            WsMoveRequest move = new Gson().fromJson(message, WsMoveRequest.class);

            WsMoveMergeRequest merge = new WsMoveMergeRequest(command, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(merge));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setChessGame (ChessGame chessGame) {
        this.chessGame = chessGame;
    }

    public ChessGame getChessGame() {
        return chessGame;
    }

}
