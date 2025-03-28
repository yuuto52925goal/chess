package client;

import chess.ChessBoard;
import ui.ChessBoardDrawer;

import java.util.Arrays;
import java.util.Scanner;

public class GameClient extends BaseClient{

    private String auth;
    private String userColor = "WHITE";
    private ChessBoard currentBoard;
    private ChessBoardDrawer chessBoardDrawer;

    public GameClient(String auth) {
        this.auth = auth;
        currentBoard = new ChessBoard();
        currentBoard.resetBoard();
        chessBoardDrawer = new ChessBoardDrawer();
    }

    @Override
    protected boolean shouldExit(String result) {
        return result.equals("Finish game");
    }

    @Override
    protected void drawBoard(){
        ChessBoardDrawer.drawChessBoard(currentBoard, userColor);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = tokens.length > 0 ? tokens[0] : "help";
            var args = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "e" -> "Finish game";
                default -> help();
            };
        }catch (Exception e){
            return e.getMessage();
        }
    }

    public String help() {
        return  """
                Options:
                - List all the games that currently exist: "<Place> <Move>"
                - Finish the game: "e", "Finish the game"
                """;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public void setUserColor(String userColor) {
        this.userColor = userColor;
    }
}
