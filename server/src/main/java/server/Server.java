package server;

import com.google.gson.Gson;
import handler.UserHandler;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.request.ListGamesRequest;
import model.result.ErrorResponse;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

    private final UserService userService;
    private final UserHandler userHandler;
    private final GameService gameService;

    public Server() {
        this.userService = new UserService();
        userHandler = new UserHandler(userService);
        gameService = new GameService();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("main/resources/web");

        // Register your endpoints and handle exceptions here.
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
            if (userHandler.authCheck(createGameRequest.token()) == null) {
                res.status(401);
                return new ErrorResponse("Error: unauthorized");
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
            ListGamesRequest listGamesRequest = new Gson().fromJson(req.body(), ListGamesRequest.class);
            if (userHandler.authCheck(listGamesRequest.token()) == null) {
                res.status(401);
                return new ErrorResponse("Error: unauthorized");
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
            JoinGameRequest joinGameRequest = new Gson().fromJson(req.body(), JoinGameRequest.class);
            if (userHandler.authCheck(joinGameRequest.token()) == null) {
                res.status(401);
                return new ErrorResponse("Error: unauthorized");
            }
            String username = userHandler.authCheck(joinGameRequest.token());
            Object result = gameService.joinGame(joinGameRequest, username);
            if (result instanceof ErrorResponse) {
                if (((ErrorResponse) result).message().equals("Error: already taken")) {
                    res.status(403);
                    return new ErrorResponse("Error: already taken");
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

//    public Object clearApp(Response res){
//        try{
//            this.userHandler.
//            res.status(200);
//            return new Gson().toJson(new Object());
//        }catch (Exception e){
//            res.status(500);
//            return new Gson().toJson(new ErrorResponse("Error: " + e.getMessage()));
//        }
//
//    }

}
