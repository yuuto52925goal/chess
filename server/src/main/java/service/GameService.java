package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;

public class GameService {

    private GameDAO gameDAO;

    public GameService() {
        gameDAO = new MemoryGameDAO();
    }


}
