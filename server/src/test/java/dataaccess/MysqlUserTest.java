package dataaccess;

import model.data.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class MysqlUserTest {
    private MysqlUserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new MysqlUserDAO();
        userDAO.deleteAllUsers();
    }

    @Test
    void testCreateUserAndGetUser() {
        String username = "testUser";
        String plainTextPassword = "plaintextPassword";
        String email = "test@example.com";
        UserData newUser = new UserData(username, plainTextPassword, email);
        userDAO.createUser(newUser);
        UserData retrievedUser = userDAO.getUser(username);
        assertNotNull(retrievedUser, "User should be retrieved after creation");
        String storedPassword = retrievedUser.password();
        assertNotEquals(plainTextPassword, storedPassword, "Stored password should be hashed");
        assertTrue(BCrypt.checkpw(plainTextPassword, storedPassword), "Hashed password should match the plain text password");
        assertEquals(email, retrievedUser.email(), "Emails should match");
    }

    @Test
    void testGetUserNonexistent() {
        UserData retrievedUser = userDAO.getUser("nonexistentUser");
        assertNull(retrievedUser, "Retrieving a non-existent user should return null");
    }

    @Test
    void testDeleteAllUsers() {
        userDAO.createUser(new UserData("user1", "password1", "user1@example.com"));
        userDAO.createUser(new UserData("user2", "password2", "user2@example.com"));
        userDAO.deleteAllUsers();
        assertNull(userDAO.getUser("user1"), "After deletion, user1 should not be found");
        assertNull(userDAO.getUser("user2"), "After deletion, user2 should not be found");
    }
}
