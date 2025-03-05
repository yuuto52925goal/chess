package dataaccess;

import model.data.GameData;

import java.util.ArrayList;

public class MysqlGameDAO extends Mysql implements GameDAO{

    public MysqlGameDAO() {

    }

    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }

    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public GameData findGame(int gameId) {
        return null;
    }

    @Override
    public void updateGame(GameData game) {

    }

    @Override
    public void deleteGames() {

    }


}
