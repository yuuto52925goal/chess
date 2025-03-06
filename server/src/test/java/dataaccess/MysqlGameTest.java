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
        gameDAO.deleteGames();
    }

    @Test
    void testListGamesEmptyPositive() {
        ArrayList<GameData> games = gameDAO.listGames();
        Assertions.assertNotNull(games, "List of games should not be null");
        Assertions.assertEquals(0, games.size(), "Game list should be empty after deletion");
    }

    @Test
    void testCreateGamePositive() {
        String gameName = "TestGame";
        int gameId = gameDAO.createGame(gameName);
        Assertions.assertTrue(gameId > 0, "createGame should return a positive gameID");
        GameData game = gameDAO.findGame(gameId);
        Assertions.assertNotNull(game, "Created game should be found by findGame");
        Assertions.assertEquals(gameName, game.gameName(), "Game name should match the one provided");
    }

    @Test
    void testCreateGameNegative() {
        String invalidGameName = "";
        int gameId = gameDAO.createGame(invalidGameName);
        Assertions.assertEquals(-1, gameId, "Creating a game with an empty gameName should return -1");
    }

    @Test
    void testFindGamePositive() {
        String gameName = "FindGameTest";
        int gameId = gameDAO.createGame(gameName);
        GameData game = gameDAO.findGame(gameId);
        Assertions.assertNotNull(game, "findGame should return a game for a valid gameID");
        Assertions.assertEquals(gameName, game.gameName(), "Game name should match the inserted game");
    }

    @Test
    void testFindGameNegative() {
        GameData game = gameDAO.findGame(-1);
        Assertions.assertNull(game, "findGame should return null for an invalid gameID");
    }

    @Test
    void testUpdateGamePositive() {
        String gameName = "UpdateGameTest";
        int gameId = gameDAO.createGame(gameName);
        GameData originalGame = gameDAO.findGame(gameId);
        Assertions.assertNotNull(originalGame, "Game should exist before update");
        GameData updatedGame = new GameData(gameId, "WhitePlayer", "BlackPlayer", gameName, null);
        gameDAO.updateGame(updatedGame);
        GameData gameAfterUpdate = gameDAO.findGame(gameId);
        Assertions.assertNotNull(gameAfterUpdate, "Game should exist after update");
        Assertions.assertEquals("WhitePlayer", gameAfterUpdate.whiteUsername(), "whiteUsername should be updated");
        Assertions.assertEquals("BlackPlayer", gameAfterUpdate.blackUsername(), "blackUsername should be updated");
    }

    @Test
    void testUpdateGameNegative() {
        GameData nonExistentGame = new GameData(-1, "White", "Black", "NonExistent", null);
        Assertions.assertDoesNotThrow(() -> gameDAO.updateGame(nonExistentGame),
                "Updating a non-existent game should not throw an exception");
        GameData game = gameDAO.findGame(-1);
        Assertions.assertNull(game, "There should be no game with an invalid gameID");
    }

    @Test
    void testDeleteGamesPositive() {
        gameDAO.createGame("Game1");
        gameDAO.createGame("Game2");
        ArrayList<GameData> gamesBeforeDelete = gameDAO.listGames();
        Assertions.assertTrue(gamesBeforeDelete.size() >= 2, "There should be at least two games created");
        gameDAO.deleteGames();
        ArrayList<GameData> gamesAfterDelete = gameDAO.listGames();
        Assertions.assertEquals(0, gamesAfterDelete.size(), "All games should be deleted");
    }
}
