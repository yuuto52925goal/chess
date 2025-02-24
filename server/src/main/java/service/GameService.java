package service;

import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import model.request.ListGamesRequest;
import model.result.ListGamesResult;

public class GameService {

    private GameDAO gameDAO;

    public GameService() {
        gameDAO = new MemoryGameDAO();
    }

//    public Object listGames(ListGamesRequest){
//        return new ListGamesResult();
//    }


}
