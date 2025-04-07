package websocket;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.MysqlAuthDAO;
import dataaccess.MysqlGameDAO;
import model.data.ConnectionData;
import model.data.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;


import java.io.IOException;
import java.util.Objects;

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
//      Get username
        String username = mysqlAuthDAO.checkAuth(userGameCommand.getAuthToken());

        connectionManager.add(new ConnectionData(userGameCommand.getAuthToken(), userGameCommand.getGameID()), userSession);

//       Get gameData put new one if it is null
        GameData gameData = mysqlGameDAO.findGame(userGameCommand.getGameID());
        if(gameData.game() == null){
            ChessBoard chessBoard = new ChessBoard();
            chessBoard.resetBoard();
            ChessGame newGame = new ChessGame();
            newGame.setBoard(chessBoard);
            GameData newGameData = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    newGame
            );
            gameData = newGameData;
            mysqlGameDAO.updateGame(newGameData);
        }

//        Check observer, white, or black
        String userColor;
        if (Objects.equals(gameData.whiteUsername(), username)){
            userColor = "white";
        }else if (Objects.equals(gameData.blackUsername(), username)){
            userColor = "black";
        }else{
            userColor = "Observer";
        }

        connectionManager.sendTo(new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME), userSession, gameData.game().getBoard(), null);
        String message = String.format("%s joined the game as %s", username, userColor);
        connectionManager.broadcast(userGameCommand.getAuthToken(), userGameCommand.getGameID(), ServerMessage.ServerMessageType.NOTIFICATION, message);
    }

    public void makeMove(UserGameCommand userGameCommand, Session userSession) throws IOException {
        String username = mysqlAuthDAO.checkAuth(userGameCommand.getAuthToken());

        GameData gameData = mysqlGameDAO.findGame(userGameCommand.getGameID());
        if(gameData.game() == null){
            return;
        }


    }

}
