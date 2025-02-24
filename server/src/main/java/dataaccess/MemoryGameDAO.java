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
        String gameId = String.valueOf(games.size());
        games.add(new GameData(gameId, "", "", gameName, null));
        return gameId;
    }

    @Override
    public GameData findGame(String gameId) {
        for (GameData gameData : games) {
            if (gameData.gameID().equals(gameId)) {
                return gameData;
            }
        }
        return null;
    }

    @Override
    public void updateGame(GameData newGame) {
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).gameID().equals(newGame.gameID())) {
                games.set(i, newGame);
            }
        }
    }

    @Override
    public void deleteGames() {
        games.clear();
    }
}
