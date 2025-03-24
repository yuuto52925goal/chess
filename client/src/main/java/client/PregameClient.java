package client;

import java.util.Arrays;
import java.util.Scanner;

public class PregameClient {

    private String auth;
    private GameClient gameClient;

    public PregameClient(String auth) {
        this.auth = auth;
        this.gameClient = new GameClient(auth);
    }

    public void run () {
        System.out.println(help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("logout")) {
            String input = scanner.nextLine();
            try {
                result = this.eval(input);
                System.out.println(result);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    public String eval (String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = tokens.length > 0 ? tokens[0] : "help";
            var args = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "l" -> listGame();
                case "c" -> createGame(args);
                case "p" -> joinGame(args);
                case "q" -> logout();
                default -> help();
            };
        }catch (Exception e){
            return e.getMessage();
        }
    }

    public String listGame(){
        return "List of games";
    }

    public String createGame (String... params){
        if (params.length < 2) {
            System.out.println(Arrays.toString(params));
            return "Create game";
        }
        return "Error";
    }

    public String joinGame(String... params) {
        if (params.length >= 2){
            System.out.println(Arrays.toString(params));
            this.gameClient.run();
            return "join game";
        }
        return "Error";
    }

    public String logout () {
        return "logout";
    }

    public String help(){
        return """
                Options:
                - List all the games that currently exist: "l"
                - Create a new game: "c" <GAMENAME>
                - Play game: "p" <PLAYERCOLOR> <GAMEID>
                - Observe game: "o" <GAMENAME>
                - Logout the program: "q", "quit"
                - Print this message: "h", "help"
                """;
    }

    public void setAuth(String auth) {
        this.auth = auth;
        this.gameClient.setAuth(auth);
    }
}
