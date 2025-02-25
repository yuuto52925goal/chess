package dataaccess;

import model.data.GameData;

import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO {

    private ArrayList<GameData> games;

    public MemoryGameDAO() {
        games = new ArrayList<>();
    }

    @Override
    public ArrayList<GameData> listGames() {
        return games;
    }

    @Override
    public int createGame(String gameName) {
        int gameId = games.size() + 1;
        games.add(new GameData(gameId, null, null, gameName, null));
        return gameId;
    }

    @Override
    public GameData findGame(int gameId) {
        for (GameData gameData : games) {
            if (gameData.gameID() == gameId) {
                return gameData;
            }
        }
        return null;
    }

    @Override
    public void updateGame(GameData newGame) {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).gameID() == newGame.gameID()) {
                games.set(i, newGame);
            }
        }
    }

    @Override
    public void deleteGames() {
        games.clear();
    }
}
