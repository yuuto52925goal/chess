package client;

import chess.ChessBoard;
import ui.ChessBoardDrawer;

import java.util.Arrays;
import java.util.Scanner;

public class GameClient {

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

    public void run() {
        System.out.println(help());
        ChessBoardDrawer.drawChessBoard(currentBoard, userColor);
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("Finish game")) {
            String input = scanner.nextLine();
            try {
                result = this.eval(input);
                System.out.println(result);
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
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
