package client;

import model.request.*;
import model.result.CreateGameResult;
import model.result.ListGamesResult;
import model.result.LoginResult;
import model.result.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() {
        facade.clearApplication();
    }

    @Test
    void clearApplicationPositive() {
        facade.registerUser(new RegisterRequest("user1", "password", "email@test.com"));

        assertDoesNotThrow(() -> facade.clearApplication());
    }

    @Test
    void registerUserPositive() {
        RegisterRequest request = new RegisterRequest("testUser", "testPass", "test@email.com");
        RegisterResult result = facade.registerUser(request);

        assertNotNull(result);
        assertNotNull(result.authToken());
        assertTrue(result.authToken().length() > 10);
        assertEquals("testUser", result.username());
    }

    @Test
    void registerUserNegative() {
        facade.registerUser(new RegisterRequest("duplicate", "pass", "email@test.com"));

        assertNull(facade.registerUser(new RegisterRequest("duplicate", "pass", "email@test.com")));
    }

    @Test
    void loginUserPositive() {
        // First register
        facade.registerUser(new RegisterRequest("loginUser", "password", "login@test.com"));

        LoginRequest request = new LoginRequest("loginUser", "password");
        LoginResult result = facade.loginUser(request);

        assertNotNull(result);
        assertNotNull(result.authToken());
        assertTrue(result.authToken().length() > 10);
        assertEquals("loginUser", result.username());
    }

    @Test
    void loginUserNegative() {
        // Try to login with invalid credentials
        LoginRequest request = new LoginRequest("nonexistent", "wrongpass");
        assertNull(facade.loginUser(request));
    }

    @Test
    void logoutUserPositive() {
        // Register and login first
        RegisterResult registerResult = facade.registerUser(
                new RegisterRequest("logoutUser", "password", "logout@test.com"));

        // Logout should complete without errors
        assertDoesNotThrow(() ->
                facade.logoutUser(new LogoutRequest(registerResult.authToken()), registerResult.authToken()));
    }

    @Test
    void logoutUserNegative() {
        assertDoesNotThrow(() -> facade.logoutUser(new LogoutRequest("hello"), "invalidToken"));
    }

    @Test
    void listGamesPositive() {
        // Register and create a game
        RegisterResult registerResult = facade.registerUser(
                new RegisterRequest("gameLister", "password", "games@test.com"));
        facade.createGame(new CreateGameRequest("TestGame"), registerResult.authToken());

        ListGamesResult result = facade.listGames(null, registerResult.authToken());

        assertNotNull(result);
        assertNotNull(result.games());
        assertEquals(1, result.games().length);
    }

    @Test
    void listGamesNegative() {
        // Try to list games without auth token
        assertNull(facade.listGames(new ListGamesRequest("invalidToken"), "invalidToken"));
    }

    @Test
    void createGamePositive() {
        // Register first
        RegisterResult registerResult = facade.registerUser(
                new RegisterRequest("gameCreator", "password", "create@test.com"));

        CreateGameRequest request = new CreateGameRequest("New Game");
        CreateGameResult result = facade.createGame(request, registerResult.authToken());

        assertNotNull(result);
        assertTrue(result.gameID() > 0);
    }

    @Test
    void createGameNegative() {
        // Try to create game without auth token
        assertNull(facade.createGame(new CreateGameRequest("Unauthorized Game"), "invalidToken"));
    }

    @Test
    void joinGamePositive() {
        RegisterResult registerResult = facade.registerUser(
                new RegisterRequest("gameJoiner", "password", "join@test.com"));
        CreateGameResult game = facade.createGame(
                new CreateGameRequest("Joinable Game"), registerResult.authToken());

        JoinGameRequest request = new JoinGameRequest(game.gameID(), "WHITE");
        assertDoesNotThrow(() ->
                facade.joinGame(request, registerResult.authToken()));
    }

    @Test
    void joinGameNegative() {
        RegisterResult registerResult = facade.registerUser(
                new RegisterRequest("gameOwner", "password", "owner@test.com"));
        CreateGameResult game = facade.createGame(
                new CreateGameRequest("Full Game"), registerResult.authToken());

        JoinGameRequest firstJoin = new JoinGameRequest(game.gameID(),"WHITE" );
        facade.joinGame(firstJoin, registerResult.authToken());

        JoinGameRequest secondJoin = new JoinGameRequest(game.gameID(), "WHITE");
        assertNull(facade.joinGame(secondJoin, registerResult.authToken()));
    }

}
