package handler;

import com.google.gson.Gson;
import model.request.CreateGameRequest;
import model.result.ErrorResponse;
import service.GameService;
import spark.Request;
import spark.Response;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object createGame(Request request, Response response) {
        try{
            CreateGameRequest createGameRequest = new Gson().fromJson(request.body(), CreateGameRequest.class);
            return null;
        }catch (Exception e){
            response.status(500);
            return new Gson().toJson(new ErrorResponse(e.getMessage()));
        }
    }
}
