package dataaccess;

import model.data.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class MysqlGameTest {
    private MysqlGameDAO gameDAO;

    @BeforeEach
    void setUp() {
        gameDAO = new MysqlGameDAO();
        // Ensure a clean slate before each test
        gameDAO.deleteGames();
    }

    @Test
    void testListGamesEmpty() {
        // When no games exist, listGames should return an empty list.
        ArrayList<GameData> games = gameDAO.listGames();
        Assertions.assertNotNull(games, "List of games should not be null");
        Assertions.assertEquals(0, games.size(), "Game list should be empty after deletion");
    }

    @Test
    void testCreateGame() {
        // Create a new game and verify that it exists in the database.
        String gameName = "TestGame";
        int gameId = gameDAO.createGame(gameName);
        Assertions.assertTrue(gameId > 0, "createGame should return a positive gameID");

        GameData game = gameDAO.findGame(gameId);
        Assertions.assertNotNull(game, "Created game should be found by findGame");
        Assertions.assertEquals(gameName, game.gameName(), "Game name should match the one provided");
    }

    @Test
    void testFindGame() {
        // Create a game, then retrieve it using findGame.
        String gameName = "FindGameTest";
        int gameId = gameDAO.createGame(gameName);
        GameData game = gameDAO.findGame(gameId);
        Assertions.assertNotNull(game, "findGame should return a game for a valid gameID");
        Assertions.assertEquals(gameName, game.gameName(), "Game name should match the inserted game");
    }

    @Test
    void testUpdateGame() {
        // Create a game, update its whiteUsername and blackUsername, then verify the update.
        String gameName = "UpdateGameTest";
        int gameId = gameDAO.createGame(gameName);
        GameData originalGame = gameDAO.findGame(gameId);
        Assertions.assertNotNull(originalGame, "Game should exist before update");

        // Prepare updated game data.
        GameData updatedGame = new GameData(gameId, "WhitePlayer", "BlackPlayer", gameName, null);
        gameDAO.updateGame(updatedGame);

        GameData gameAfterUpdate = gameDAO.findGame(gameId);
        Assertions.assertNotNull(gameAfterUpdate, "Game should exist after update");
        Assertions.assertEquals("WhitePlayer", gameAfterUpdate.whiteUsername(), "whiteUsername should be updated");
        Assertions.assertEquals("BlackPlayer", gameAfterUpdate.blackUsername(), "blackUsername should be updated");
    }

    @Test
    void testDeleteGames() {
        // Create multiple games.
        gameDAO.createGame("Game1");
        gameDAO.createGame("Game2");

        // Verify that games exist.
        ArrayList<GameData> gamesBeforeDelete = gameDAO.listGames();
        Assertions.assertTrue(gamesBeforeDelete.size() >= 2, "There should be at least two games created");

        // Delete all games.
        gameDAO.deleteGames();
        ArrayList<GameData> gamesAfterDelete = gameDAO.listGames();
        Assertions.assertEquals(0, gamesAfterDelete.size(), "All games should be deleted");
    }
}
