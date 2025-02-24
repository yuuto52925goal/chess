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
    public String createGame(String gameName) {
        int gameId = games.size();
        games.add(new GameData(gameId, null, null, gameName, null));
        return String.valueOf(gameId);
    }

    @Override
    public GameData findGame(String gameName) {
        for (GameData gameData : games) {
            if (gameData.gameName().equals(gameName)) {
                return gameData;
            }
        }
        return null;
    }

    @Override
    public void updateGame(GameData newGame) {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).gameName().equals(newGame.gameName())) {
                games.set(i, newGame);
            }
        }
    }

    @Override
    public void deleteGames() {
        games.clear();
    }
}
