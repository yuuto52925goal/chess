package client;

import chess.ChessBoard;
import com.google.gson.Gson;
import server.WebSocketFacade;
import ui.ChessBoardDrawer;
import websocket.commands.UserGameCommand;

import java.util.Arrays;
import java.util.Scanner;

public class GameClient extends BaseClient{

    private String auth;
    private String userColor = "WHITE";
    private Integer gameID;
    private ChessBoard currentBoard;
    private ChessBoardDrawer chessBoardDrawer;
    private WebSocketFacade webSocketFacade;

    public GameClient(String auth) {
        this.auth = auth;
        currentBoard = new ChessBoard();
        currentBoard.resetBoard();
        chessBoardDrawer = new ChessBoardDrawer();
    }

    @Override
    protected boolean shouldExit(String result) {
        return result.equals("leave");
    }

    @Override
    protected void drawBoard(){
        ChessBoardDrawer.drawChessBoard(currentBoard, userColor);
    }

    public void run(WebSocketFacade webSocketFacade) {
        this.webSocketFacade = webSocketFacade;
        run();
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = tokens.length > 0 ? tokens[0] : "help";
            var args = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "r" -> redraw();
                case "l" -> leave();
                case "m" -> makeMove(args);
                case "re" -> resign(args);
                case "h" -> highlight(args);
                default -> help();
            };
        }catch (Exception e){
            return e.getMessage();
        }
    }

    public String redraw() {
        drawBoard();
        return "Draw board";
    }

    public String leave() {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, gameID);
        this.webSocketFacade.runUserCommand(new Gson().toJson(command));
        return "leave";
    }

    public String makeMove(String... params) {
        return "makeMove";
    }

    public String resign(String... params) {
        return "resign";
    }

    public String highlight(String... params) {
        return "highlight";
    }


    public String help() {
        return  """
                Options:
                - Show help command: "help"
                - Redraw Chess Board: "r"
                - Leave Chess Board: "l"
                - Make Move: "m start end"
                - Resign Chess Board: "re"
                - Highlight legal moves: "h"
                """;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public void setUserColor(String userColor) {
        this.userColor = userColor;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }
}
