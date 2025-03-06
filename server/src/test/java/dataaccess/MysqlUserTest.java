package dataaccess;

import model.data.UserData;
import org.junit.jupiter.api.Assertions;
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
    void testCreateUserAndGetUser_Positive() {
        String username = "testUser";
        String plainTextPassword = "plaintextPassword";
        String email = "test@example.com";
        UserData newUser = new UserData(username, plainTextPassword, email);
        userDAO.createUser(newUser);
        UserData retrievedUser = userDAO.getUser(username);
        Assertions.assertNotNull(retrievedUser, "User should be retrieved after creation");
        String storedPassword = retrievedUser.password();
        Assertions.assertNotEquals(plainTextPassword, storedPassword, "Stored password should be hashed");
        Assertions.assertTrue(BCrypt.checkpw(plainTextPassword, storedPassword),
                "The hashed password should match the plain text password");
        Assertions.assertEquals(email, retrievedUser.email(), "Emails should match");
    }

    @Test
    void testGetUser_Negative() {
        UserData retrievedUser = userDAO.getUser("nonexistentUser");
        Assertions.assertNull(retrievedUser, "Retrieving a non-existent user should return null");
    }

    @Test
    void testCreateUser_DuplicateUsername_Negative() {
        String username1 = "user1";
        String username2 = "user2";
        String password = "password1";
        String email = "email1@example.com";

        UserData user1 = new UserData(username1, password, email);
        UserData user2 = new UserData(username2, password, email);

        userDAO.createUser(user1);
        Assertions.assertTrue(userDAO.getUser(user2.username()) == null, "User should not be created");
    }

    @Test
    void testDeleteAllUsers_Positive() {
        userDAO.createUser(new UserData("user1", "password1", "user1@example.com"));
        userDAO.createUser(new UserData("user2", "password2", "user2@example.com"));

        userDAO.deleteAllUsers();
        Assertions.assertNull(userDAO.getUser("user1"), "User1 should be deleted");
        Assertions.assertNull(userDAO.getUser("user2"), "User2 should be deleted");
    }
}
