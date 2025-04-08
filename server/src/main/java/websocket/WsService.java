package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import chess.InvalidMoveException;
import dataaccess.MysqlAuthDAO;
import dataaccess.MysqlGameDAO;
import model.data.ConnectionData;
import model.data.GameData;
import model.request.WsMoveRequest;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorResponse;
import websocket.messages.LoadResponse;
import websocket.messages.NotifiResponse;
import websocket.messages.ServerMessage;


import java.io.IOException;

public class WsService {
    private final MysqlAuthDAO mysqlAuthDAO;
    private final MysqlGameDAO mysqlGameDAO;
    private final ConnectionManager connectionManager;

    public WsService(ConnectionManager connectionManager) {
        this.mysqlAuthDAO = new MysqlAuthDAO();
        this.mysqlGameDAO = new MysqlGameDAO();
        this.connectionManager = connectionManager;
    }

    public void joinGame(UserGameCommand userGameCommand, Session userSession) throws IOException {

        String username = validateRequest(userGameCommand, userSession);
        if (username == null) {
            return;
        }
        GameData gameData = mysqlGameDAO.findGame(userGameCommand.getGameID());
        String userColor = determineUserColor(gameData, username);

        System.out.println(username + "logged in" + userColor + " as userColor");
//        Send the message himself
        connectionManager.add(new ConnectionData(userGameCommand.getAuthToken(), userGameCommand.getGameID()), userSession);
        ServerMessage.ServerMessageType type = ServerMessage.ServerMessageType.LOAD_GAME;
        LoadResponse loadResponse = new LoadResponse(type, gameData);
        connectionManager.sendTo(userSession, loadResponse);
//        Send other users
        String message = String.format("%s joined the game as %s", username, userColor);
        NotifiResponse notifiResponse = new NotifiResponse(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connectionManager.broadcast(userGameCommand.getAuthToken(), userGameCommand.getGameID(), ServerMessage.ServerMessageType.NOTIFICATION, notifiResponse, null);
    }

    public void makeMove(UserGameCommand userGameCommand, Session userSession, WsMoveRequest wsMoveRequest) throws IOException, InvalidMoveException {
        String username = validateRequest(userGameCommand, userSession);
        if (username == null) {
            return;
        }

        GameData gameData = mysqlGameDAO.findGame(userGameCommand.getGameID());
        String userColor = determineUserColor(gameData, username);
        if (userColor.equals("OBSERVER")) {
            sendError(userSession, "Cannot make a move because you are observer");
            return;
        }

        String turn = gameData.game().getTeamTurn() == ChessGame.TeamColor.WHITE ? "WHITE" : "BLACK";
        if (!userColor.equals(turn)) {
            sendError(userSession, "Cannot make a move because you are observer");
            return;
        }

        try {
            gameData.game().makeMove(wsMoveRequest.move());
        }catch (InvalidMoveException e) {
            sendError(userSession, "Invalid move");
            return;
        }

        mysqlGameDAO.updateGame(gameData);
        LoadResponse loadResponse = new LoadResponse(ServerMessage.ServerMessageType.LOAD_GAME, gameData);
        connectionManager.sendTo(userSession, loadResponse);
        String message = String.format("%s maked the move as %s", username, userColor);
        NotifiResponse notifiResponse = new NotifiResponse(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connectionManager.broadcast(userGameCommand.getAuthToken(), userGameCommand.getGameID(), ServerMessage.ServerMessageType.NOTIFICATION, notifiResponse, loadResponse);
    }


    private String validateRequest (UserGameCommand userGameCommand, Session userSession) throws IOException {

        String username = mysqlAuthDAO.checkAuth(userGameCommand.getAuthToken());
        if (username == null) {
            sendError(userSession, "No right auth token");
            return null;
        }

        GameData gameData = mysqlGameDAO.findGame(userGameCommand.getGameID());
        if (gameData == null) {
            sendError(userSession, "Game not found");
            return null;
        }

        if (gameData.game() == null){
            initializeNewGame(gameData);
        }

        return username;
    }

    private String determineUserColor(GameData gameData, String username) {
        if (username.equals(gameData.whiteUsername())) {
            return "WHITE";
        } else if (username.equals(gameData.blackUsername())) {
            return "BLACK";
        }
        return "OBSERVER";
    }

    private void initializeNewGame(GameData gameData) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        ChessGame game = new ChessGame();
        game.setBoard(board);

        GameData newData = new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                game
        );
        mysqlGameDAO.updateGame(newData);
    }

    private void sendError(Session session, String message) throws IOException {
        connectionManager.sendTo(session,
                new ErrorResponse(ServerMessage.ServerMessageType.ERROR, message));
    }

}
