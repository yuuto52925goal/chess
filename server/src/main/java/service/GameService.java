package service;

import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import model.data.GameData;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.result.CreateGameResult;
import model.result.ErrorResponse;
import model.result.JoinGameResult;
import model.result.ListGamesResult;

import java.util.Objects;

public class GameService {

    private GameDAO gameDAO;

    public GameService() {
        gameDAO = new MemoryGameDAO();
    }

    public Object listGames(){
        return new ListGamesResult(gameDAO.listGames().toArray(new GameData[0]));
    }

    public Object createGame(CreateGameRequest createGameRequest){
        String gameName = createGameRequest.gameName();
        if (gameName == null || gameName.isEmpty()) {
            return new ErrorResponse("Error: bad request");
        }
        return new CreateGameResult(gameDAO.createGame(gameName));
    }

    public Object joinGame(JoinGameRequest joinGameRequest, String username){
        GameData findGame = gameDAO.findGame(joinGameRequest.gameId());
        if (findGame == null) {
            return new ErrorResponse("Error: bad request");
        }
        boolean checkTaken = Objects.equals(joinGameRequest.playerColor(), "BLACK") ? !findGame.blackUsername().isEmpty(): !findGame.whiteUsername().isEmpty();
        if (checkTaken) {
            return new ErrorResponse("Error: already taken");
        }
        GameData newGameData = Objects.equals(joinGameRequest.playerColor(), "BLACK") ? new GameData(findGame.gameID(), findGame.whiteUsername(), username, findGame.gameName(), findGame.game()):
                new GameData(findGame.gameID(), username, findGame.blackUsername(), findGame.gameName(), findGame.game());
        gameDAO.updateGame(newGameData);
        return new JoinGameResult();
    }

}
