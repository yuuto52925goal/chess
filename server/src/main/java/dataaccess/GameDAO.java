package dataaccess;

import model.data.GameData;

import java.util.ArrayList;

public interface GameDAO {

    public ArrayList<GameData> listGames();

    public int createGame(String gameName);

    public GameData findGame(String gameName);

    public void updateGame(GameData game);

    public void deleteGames();

}
