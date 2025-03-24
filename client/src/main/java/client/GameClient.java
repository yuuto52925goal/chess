package client;

import chess.ChessBoard;

import java.util.Arrays;
import java.util.Scanner;

public class GameClient {

    private String auth;
    private String userColor = "WHITE";
    private ChessBoard currentBoard;

    public GameClient(String auth) {
        this.auth = auth;
        currentBoard = null;
    }

    public void run() {
        System.out.println(help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("exit")) {
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
                case "e" -> "exit";
                default -> "hello";
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

    public String setUserColor(String userColor) {
        this.userColor = userColor;
    }
}
