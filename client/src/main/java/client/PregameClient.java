package client;

import chess.ChessGame;
import model.data.GameData;
import model.request.*;
import model.result.CreateGameResult;
import model.result.ListGamesResult;
import server.ServerFacade;



import java.util.Arrays;

public class PregameClient extends BaseClient {

    private String auth;
    private GameClient gameClient;
    private ServerFacade serverFacade;

    public PregameClient(String auth, String url) {
        this.auth = auth;
        this.gameClient = new GameClient(auth);
        this.serverFacade = new ServerFacade(url);
    }

    @Override
    protected boolean shouldExit(String result) {
        return result.equals("logging out from the system...");
    }

    @Override
    protected void drawBoard(){

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
//                case "o" -> joinGame(args);
                case "q" -> logout();
                default -> help();
            };
        }catch (Exception e){
            return e.getMessage();
        }
    }

    public String listGame(){
        var result = serverFacade.listGames(null, auth);
        if (result == null){
            return "Error listing games";
        }
        for (GameData game: ((ListGamesResult)result).games()){
            System.out.println(game.gameName() + "White username" + game.whiteUsername() + "Black username" + game.blackUsername());
        }
        return "Showing list of games";
    }

    public String createGame (String... params){
        if (params.length >= 1) {
            CreateGameRequest createGameRequest = new CreateGameRequest(params[0]);
            var result = serverFacade.createGame(createGameRequest, auth);
            if (result == null){
                return "Error creating game";
            }
            System.out.println(((CreateGameResult) result).gameID() + " is created!");
            return "Created game";
        }
        return "Error";
    }

    public String joinGame(String... params) {
        if (params.length >= 2){
            if (params[1].equals("white") || params[1].equals("black")){
                JoinGameRequest joinGameRequest = new JoinGameRequest(Integer.parseInt(params[0]), params[1].toUpperCase());
                var result = serverFacade.joinGame(joinGameRequest, auth);
                if (result == null){
                    return "Error joining game";
                }
                this.gameClient.setUserColor(params[1]);
                this.gameClient.run();
                return "joined game";
            }else{
                return "Error" + params[1] + " is not a valid color";
            }
        }
        return "Error";
    }

    public String logout () {
        serverFacade.logoutUser(null, auth);
        return "logging out from the system...";
    }

    public String help(){
        return """
                Options:
                - List all the games that currently exist: "l"
                - Create a new game: "c" <GAMENAME>
                - Play game: "p" <GAMEID> <PLAYERCOLOR>
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
