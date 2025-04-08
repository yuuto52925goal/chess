package client;

import com.google.gson.Gson;
import model.data.GameData;
import model.request.*;
import model.result.ListGamesResult;
import server.ServerFacade;
import client.ws.WebSocketFacade;
import websocket.commands.UserGameCommand;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PregameClient extends BaseClient {

    private String auth;
    private GameClient gameClient;
    private ServerFacade serverFacade;
    private HashMap<Integer, Integer> gameIndex;
    private int currentGame;
    private WebSocketFacade webSocketFacade;
    private String url;

    public PregameClient(String auth, String url) {
        this.auth = auth;
        this.gameClient = new GameClient(auth);
        this.serverFacade = new ServerFacade(url);
        this.gameIndex = new HashMap<>();
        this.currentGame = 1;
        this.url = url;
    }

    @Override
    protected boolean shouldExit(String result) {
        return result.equals("logging out from the system...");
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
                case "o" -> observeBoard(args);
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
            if (!gameIndex.containsKey(game.gameID())){
                gameIndex.put(game.gameID(), currentGame);
                currentGame++;
            }
            System.out.println(
                    gameIndex.get(game.gameID()) +
                    ". " + game.gameName() +
                    ". White: " + game.whiteUsername() +
                    ". Black: " + game.blackUsername()
            );
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
            gameIndex.put(result.gameID(), currentGame);
            System.out.println(currentGame + " is created!");
            currentGame++;
            return "Created game";
        }
        return "Error";
    }

    public String joinGame(String... params) {
        if (params.length < 2){
            return "Error";
        }
        if (params[1].equals("white") || params[1].equals("black")){
            int gameIndexNum = Integer.parseInt(params[0]);
            for (Map.Entry<Integer, Integer> entry: gameIndex.entrySet()){
                if (entry.getValue() == gameIndexNum){
                    JoinGameRequest joinGameRequest = new JoinGameRequest(entry.getKey(), params[1].toUpperCase());
                    var result = serverFacade.joinGame(joinGameRequest, auth);
                    if (result == null){
                        return "Error joining game";
                    }
                    this.gameClient.setUserColor(params[1]);
                    this.gameClient.setGameID(joinGameRequest.gameID());
                    UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, auth, joinGameRequest.gameID());
                    webSocketFacade = new WebSocketFacade(url, params[1]);
                    webSocketFacade.runUserCommand(new Gson().toJson(command));
                    this.gameClient.run(webSocketFacade);
                    return "joined game";
                }
            }
            return "Error joining game";
        }else{
            return "Error " + params[1] + " is not a valid color";
        }
    }

    public String observeBoard(String... params) {
        if (params.length >= 1){
            this.gameClient.setUserColor("white");
            int gameIndexNum = Integer.parseInt(params[0]);
            for (Map.Entry<Integer, Integer> entry: gameIndex.entrySet()){
                if (entry.getValue() == gameIndexNum){
                    this.gameClient.setGameID(entry.getKey());
                }
            }
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, auth, gameIndexNum);
            webSocketFacade = new WebSocketFacade(url, "");
            webSocketFacade.runUserCommand(new Gson().toJson(command));
            this.gameClient.run(webSocketFacade);
            return "observed game";
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
