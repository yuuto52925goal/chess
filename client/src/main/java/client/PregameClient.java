package client;

import model.data.GameData;
import model.request.*;
import model.result.CreateGameResult;
import model.result.JoinGameResult;
import model.result.ListGamesResult;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class PregameClient {

    private String auth;
    private GameClient gameClient;
    private ServerFacade serverFacade;

    public PregameClient(String auth, String url) {
        this.auth = auth;
        this.gameClient = new GameClient(auth);
        this.serverFacade = new ServerFacade(url);
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
        ListGamesRequest listGamesRequest = new ListGamesRequest(auth);
        ListGamesResult listGamesResult = serverFacade.listGames(listGamesRequest, auth);
        for (GameData game: listGamesResult.games()){
            System.out.println(game);
        }
        return "List of games";
    }

    public String createGame (String... params){
        if (params.length >= 1) {
            CreateGameRequest createGameRequest = new CreateGameRequest(params[0]);
            CreateGameResult createGameResult = serverFacade.createGame(createGameRequest, auth);
            System.out.println(createGameResult.gameID() + " is created!");
            return "Create game";
        }
        return "Error";
    }

    public String joinGame(String... params) {
        if (params.length >= 2){
            System.out.println(Arrays.toString(params));
            JoinGameRequest joinGameRequest = new JoinGameRequest(Integer.parseInt(params[0]), params[1]);
            serverFacade.joinGame(joinGameRequest, auth);
            this.gameClient.run();
            return "join game";
        }
        return "Error";
    }

    public String logout () {
        LogoutRequest logoutRequest = new LogoutRequest(auth);
        serverFacade.logoutUser(logoutRequest, auth);
        return "logout";
    }

    public String help(){
        return """
                Options:
                - List all the games that currently exist: "l"
                - Create a new game: "c" <GAMENAME>
                - Play game: "p" <PLAYERCOLOR> <GAMEID>
                - Observe game: "o" <GAMENAME>
                - Logout the program: "q"
                - Print this message: "h"
                """;
    }

    public void setAuth(String auth) {
        this.auth = auth;
        this.gameClient.setAuth(auth);
    }
}
