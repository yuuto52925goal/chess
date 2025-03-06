package dataaccess;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MysqlAuthTest {
    private MysqlAuthDAO authDAO;

    @BeforeEach
    void setUp() {
        authDAO = new MysqlAuthDAO();
        authDAO.deleteAllAuths();
    }

    @Test
    void testCreateAndCheckAuthPositive() {
        String username = "TestUser";
        String authToken = authDAO.createAuth(username);
        Assertions.assertNotNull(authToken, "Auth token should not be null");
        String retrievedUsername = authDAO.checkAuth(authToken);
        Assertions.assertEquals(username, retrievedUsername, "The retrieved username should match the created one");
    }

    @Test
    void testCheckAuthNegative() {
        String invalidToken = "invalid_token";
        String result = authDAO.checkAuth(invalidToken);
        Assertions.assertNull(result, "checkAuth should return null for an invalid token");
    }

    @Test
    void testDeleteAuthPositive() {
        String username = "TestUser";
        String authToken = authDAO.createAuth(username);
        Assertions.assertNotNull(authToken, "Auth token should not be null");
        Assertions.assertEquals(username, authDAO.checkAuth(authToken), "Username should be retrievable before deletion");
        authDAO.deleteAuth(authToken);
        Assertions.assertNull(authDAO.checkAuth(authToken), "Auth token should be deleted and return null");
    }

    @Test
    void testDeleteAuthNegative() {
        Assertions.assertDoesNotThrow(() -> authDAO.deleteAuth("non_existent_token"));
    }

    @Test
    void testDeleteAllAuthsPositive() {
        authDAO.createAuth("User1");
        authDAO.createAuth("User2");
        authDAO.deleteAllAuths();
        Assertions.assertNull(authDAO.checkAuth("any_token"), "After deletion, checkAuth should return null for any token");
    }
}
