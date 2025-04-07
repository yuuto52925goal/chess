package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import handler.UserHandler;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.request.ListGamesRequest;
import model.result.ErrorResponse;
import service.GameService;
import service.UserService;
import spark.*;
import websocket.WebsocketHandler;

import java.sql.SQLException;

public class Server {

    private final UserService userService;
    private final UserHandler userHandler;
    private final GameService gameService;
    private final WebsocketHandler websocketHandler;

    public Server() {
        this.userService = new UserService();
        userHandler = new UserHandler(userService);
        gameService = new GameService();
        websocketHandler = new WebsocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");


        Spark.webSocket("/ws", websocketHandler);

        Spark.get("/", (req, res) -> {
            res.redirect("/index.html");
            return null;
        });
        Spark.delete("/db", this::clearApp);
        Spark.post("/user", userHandler::handleRegister);
        Spark.post("/session", userHandler::handleLogin);
        Spark.delete("/session", userHandler::handleLogout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public Object createGame(Request req, Response res){
        try{
            CreateGameRequest createGameRequest = new Gson().fromJson(req.body(), CreateGameRequest.class);
            if (userHandler.authCheck(req.headers("authorization")) == null) {
                res.status(401);
                return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
            }
            Object result = gameService.createGame(createGameRequest);
            if (result instanceof ErrorResponse) {
                res.status(400);
                return new Gson().toJson(result);
            }
            res.status(200);
            return new Gson().toJson(result);
        }catch (Exception e){
            res.status(500);
            return new Gson().toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public Object listGames(Request req, Response res){
        try{
            ListGamesRequest listGamesRequest = new ListGamesRequest(req.headers("authorization"));
            if (userHandler.authCheck(listGamesRequest.token()) == null) {
                res.status(401);
                return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
            }
            Object result = gameService.listGames();
            return new Gson().toJson(result);
        }catch (Exception e){
            res.status(500);
            return new Gson().toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public Object joinGame(Request req, Response res){
        try{
            String authToken  = req.headers("authorization");
            JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
            String username = userHandler.authCheck(authToken);
            if (username == null) {
                res.status(401);
                return new Gson().toJson(new ErrorResponse("Error: unauthorized"));
            }
            Object result = gameService.joinGame(joinGameRequest, username);
            if (result instanceof ErrorResponse) {
                if (((ErrorResponse) result).message().equals("Error: already taken")) {
                    res.status(403);
                    return new Gson().toJson(new ErrorResponse("Error: already taken"));
                }else{
                    res.status(400);
                    return new Gson().toJson(result);
                }
            }
            res.status(200);
            return new Gson().toJson(result);
        }catch (Exception e){
            res.status(500);
            return new Gson().toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    public Object clearApp(Request req, Response res){
        try{
            this.userHandler.clear();
            this.gameService.gameClear();
            res.status(200);
            return new Gson().toJson(new Object());
        }catch (Exception e){
            res.status(500);
            return new Gson().toJson(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

}
