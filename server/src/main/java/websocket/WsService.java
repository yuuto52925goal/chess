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
import java.util.Map;

import static java.util.Map.entry;

public class WsService {
    private final MysqlAuthDAO mysqlAuthDAO;
    private final MysqlGameDAO mysqlGameDAO;
    private final ConnectionManager connectionManager;
    private Map<Integer, String> intToC = Map.ofEntries(
            entry(1, "a"),
            entry(2, "b"),
            entry(3, "c"),
            entry(4, "d"),
            entry(5, "e"),
            entry(6, "f"),
            entry(7, "g"),
            entry(8, "h")
    );

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
        System.out.println("userColor: " + userColor);

        System.out.println(username + " logged in " + userColor + " as userColor");
//        Send the message himself
        connectionManager.add(new ConnectionData(userGameCommand.getAuthToken(), userGameCommand.getGameID()), userSession);
        ServerMessage.ServerMessageType type = ServerMessage.ServerMessageType.LOAD_GAME;
        LoadResponse loadResponse = new LoadResponse(type, gameData);
        connectionManager.sendTo(userSession, loadResponse);
//        Send other users
        String message = String.format("%s joined the game as %s", username, userColor);
        NotifiResponse notifiResponse = new NotifiResponse(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connectionManager.broadcast(
                userGameCommand.getAuthToken(), userGameCommand.getGameID(),
                ServerMessage.ServerMessageType.NOTIFICATION, notifiResponse, null
        );
    }

    public void makeMove(UserGameCommand userGameCommand, Session userSession, WsMoveRequest wsMoveRequest) throws IOException, InvalidMoveException {
        System.out.println("Make move");
        String username = validateRequest(userGameCommand, userSession);
        if (username == null) {
            System.out.println(userGameCommand.getAuthToken());
            System.out.println("username is null");
            return;
        }
        GameData gameData = mysqlGameDAO.findGame(userGameCommand.getGameID());
        String userColor = determineUserColor(gameData, username);
        if (userColor.equals("OBSERVER")) {
            System.out.println("userColor is OBSERVER");
            sendError(userSession, "Cannot make a move because you are observer");
            return;
        }
        if (gameData.game().getGameStatus()){
            System.out.println("gameData is OBSERVER");
            sendError(userSession, "Cannot make a move because it is resigned");
            return;
        }
        String turn = gameData.game().getTeamTurn() == ChessGame.TeamColor.WHITE ? "WHITE" : "BLACK";
        if (!userColor.equals(turn)) {
            System.out.println(userColor);
            System.out.println("invalid userColor");
            sendError(userSession, "Cannot make a move because invalid userColor");
            return;
        }
        try {
            gameData.game().makeMove(wsMoveRequest.move());
        }catch (InvalidMoveException e) {
            sendError(userSession, "Invalid move");
            System.out.println("Invalid move");
            return;
        }

        mysqlGameDAO.updateGame(gameData);
        LoadResponse loadResponse = new LoadResponse(ServerMessage.ServerMessageType.LOAD_GAME, gameData);
        connectionManager.sendTo(userSession, loadResponse);
        System.out.println(wsMoveRequest.move().getStartPosition() + " start " + wsMoveRequest.move().getEndPosition() + " end");
        String c1 = String.valueOf(wsMoveRequest.move().getStartPosition().getRow());
        String intToC1 = intToC.get(wsMoveRequest.move().getStartPosition().getColumn());
        String c2 = String.valueOf(wsMoveRequest.move().getEndPosition().getRow());
        String intToC2 = intToC.get(wsMoveRequest.move().getEndPosition().getColumn());
        ChessGame.TeamColor teamColor = userColor.equals("BLACK") ? ChessGame.TeamColor.WHITE: ChessGame.TeamColor.BLACK;
        String message = String.format("%s maked the move as %s. %s%s to %s%s", username, userColor, c1, intToC1, c2, intToC2);
        if (gameData.game().isInCheckmate(teamColor)){
            System.out.println("Checkmate");
            String checkmateUser = teamColor.equals(ChessGame.TeamColor.WHITE) ? gameData.blackUsername() : gameData.whiteUsername();
            message = String.format("%s is in Checkmate", username, checkmateUser);
        }else if (gameData.game().isInCheck(teamColor)){
            System.out.println("Checkin");
            String checkInUser = teamColor.equals(ChessGame.TeamColor.WHITE) ? gameData.blackUsername() : gameData.whiteUsername();
            message = String.format("%s is in Check", username, checkInUser);
        }
        NotifiResponse notifiResponse = new NotifiResponse(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connectionManager.broadcast(
                userGameCommand.getAuthToken(), userGameCommand.getGameID(),
                ServerMessage.ServerMessageType.NOTIFICATION, notifiResponse, loadResponse
        );
    }

    public void resign(UserGameCommand userGameCommand, Session userSession) throws IOException {
        String username = validateRequest(userGameCommand, userSession);
        if (username == null) {
            sendError(userSession, "Cannot resign");
            return;
        }
        GameData gameData = mysqlGameDAO.findGame(userGameCommand.getGameID());
        String userColor = determineUserColor(gameData, username);
        if (userColor.equals("OBSERVER")) {
            sendError(userSession, "Cannot resign because you are observer");
            return;
        }
        if (gameData.game().getGameStatus()){
            sendError(userSession, "Cannot resign because it is resigned");
            return;
        }
        gameData.game().setGameStatus(true);
        mysqlGameDAO.updateGame(gameData);
        System.out.println(mysqlGameDAO.findGame(userGameCommand.getGameID()).game().getGameStatus());

        String message = String.format("%s lost since resigned as %s", username, userColor);
        NotifiResponse notifiResponse = new NotifiResponse(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connectionManager.sendTo(userSession, notifiResponse);
        connectionManager.broadcast(
                userGameCommand.getAuthToken(), userGameCommand.getGameID(),
                ServerMessage.ServerMessageType.NOTIFICATION, notifiResponse, null
        );
    }

    public void leaveGame(UserGameCommand userGameCommand, Session userSession) throws IOException {
        String username = validateRequest(userGameCommand, userSession);
        if (username == null) {
            sendError(userSession, "Cannot leave the game");
            return;
        }
        GameData gameData = mysqlGameDAO.findGame(userGameCommand.getGameID());
        String userColor = determineUserColor(gameData, username);
        GameData newGameData = new GameData(
                gameData.gameID(),
                userColor.equals("WHITE") ? null: gameData.whiteUsername(),
                userColor.equals("BLACK") ? null: gameData.blackUsername(),
                gameData.gameName(),
                gameData.game()
        );
        mysqlGameDAO.updateGame(newGameData);

        String message = String.format("%s left the game as %s", username, userColor);
        ServerMessage.ServerMessageType type = ServerMessage.ServerMessageType.NOTIFICATION;
        NotifiResponse notifiResponse = new NotifiResponse(type, message);
        connectionManager.remove(userGameCommand.getAuthToken());
        connectionManager.broadcast(
                userGameCommand.getAuthToken(), userGameCommand.getGameID(), type, notifiResponse, null
        );
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
