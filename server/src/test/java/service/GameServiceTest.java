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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class GameServiceTest {

    private GameService gameService;
    private GameDAO gameDAO;

    @BeforeEach
    void setUp() {
        gameService = new GameService();
        gameDAO = new MemoryGameDAO();
    }

    @Test
    void testListGames(){
        Object noResult = gameService.listGames();
        Assertions.assertInstanceOf(ListGamesResult.class, noResult);
        Assertions.assertEquals(0, ((ListGamesResult) noResult).games().length);
    }

    @Test
    void testCreateGameSuccess(){
        CreateGameRequest request = new CreateGameRequest("TestGame");
        Object result = gameService.createGame(request);
        Assertions.assertInstanceOf(CreateGameResult.class, result);
        Assertions.assertEquals("TestGame", ((ListGamesResult)gameService.listGames()).games()[0].gameName());
    }

    @Test
    void createGameFailure(){
        CreateGameRequest request = new CreateGameRequest("");
        Object result = gameService.createGame(request);
        Assertions.assertInstanceOf(ErrorResponse.class, result);
        Assertions.assertEquals("Error: bad request", ((ErrorResponse)result).message());
    }

    @Test
    void joinGameSuccess(){
        CreateGameRequest createRequest = new CreateGameRequest("TestGame");
        gameService.createGame(createRequest);

        JoinGameRequest request = new JoinGameRequest(10, "BLACK");
        Object result = gameService.joinGame(request, "Player1");
        Assertions.assertInstanceOf(JoinGameResult.class, result);

        GameData existingGame = new GameData(10, null, "Player1", "TestGame", null);

        Assertions.assertEquals(((ListGamesResult)gameService.listGames()).games()[0], existingGame);
    }

    @Test
    void joinGameFailure(){
        CreateGameRequest createRequest = new CreateGameRequest("TestGame");
        gameService.createGame(createRequest);

        JoinGameRequest request1 = new JoinGameRequest(0, "BLACK");
        Object result = gameService.joinGame(request1, "Player1");
        Assertions.assertInstanceOf(ErrorResponse.class, result);
        Assertions.assertEquals("Error: bad request", ((ErrorResponse)result).message());

        JoinGameRequest request2 = new JoinGameRequest(10, "BLACK");
        gameService.joinGame(request2, "Player1");
        Object result3 = gameService.joinGame(request2, "Player2");
        Assertions.assertEquals("Error: already taken", ((ErrorResponse)result3).message());
    }

    @Test
    void gameClear(){
        gameService.gameClear();
        Assertions.assertEquals(((ListGamesResult)new GameService().listGames()).games().length, ((ListGamesResult)gameService.listGames()).games().length);
    }
}
