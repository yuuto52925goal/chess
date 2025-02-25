package service;

import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.result.ErrorResponse;
import model.result.LoginResult;
import model.result.LogoutResult;
import model.result.RegisterResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    void register_Success(){
        RegisterRequest registerRequest = new RegisterRequest("testUser", "password", "test@email.com");
        Object result = userService.register(registerRequest);

        Assertions.assertInstanceOf(RegisterResult.class, result);
        Assertions.assertEquals("testUser", ((RegisterResult) result).username());
        Assertions.assertNotNull(((RegisterResult) result).authToken());
    }

    @Test
    void register_Fail_taken(){
        RegisterRequest registerRequest = new RegisterRequest("testUser", "password", "test@email.com");
        userService.register(registerRequest);
        Object result2 = userService.register(registerRequest);
        Assertions.assertEquals("Error: already taken", ((ErrorResponse) result2).message());
    }

    @Test
    void register_Fail_null(){
        RegisterRequest registerRequest = new RegisterRequest("testUser", null, "test@email.com");
        Object result = userService.register(registerRequest);
        Assertions.assertEquals("Error: bad request", ((ErrorResponse) result).message());
    }

    @Test
    void login_Success(){
        RegisterRequest registerRequest = new RegisterRequest("testUser", "password", "test@email.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("testUser", "password");
        Object result2 = userService.login(loginRequest);
        Assertions.assertInstanceOf(LoginResult.class, result2);
        Assertions.assertEquals("testUser", ((LoginResult) result2).username());
        Assertions.assertNotNull(((LoginResult) result2).authToken());
    }

    @Test
    void login_Fail(){
        RegisterRequest registerRequest = new RegisterRequest("testUser", "password", "test@email.com");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("testUser", "password-password");
        Object result = userService.login(loginRequest);
        Assertions.assertEquals("Error: unauthorized", ((ErrorResponse) result).message());
    }

    @Test
    void logout_Success(){
        RegisterRequest registerRequest = new RegisterRequest("testUser", "password", "test@email.com");
        Object result1 = userService.register(registerRequest);
        RegisterResult registerResult = (RegisterResult) result1;

        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        Object result2 = userService.logout(logoutRequest);
        Assertions.assertInstanceOf(LogoutResult.class, result2);

        Assertions.assertNull(userService.authCheck(registerResult.authToken()));
    }

    @Test
    void logout_Fail(){
        RegisterRequest registerRequest = new RegisterRequest("testUser", "password", "test@email.com");
        Object result1 = userService.register(registerRequest);
        RegisterResult registerResult = (RegisterResult) result1;
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken() + " ");
        Object result2 = userService.logout(logoutRequest);
        Assertions.assertEquals("Error: unauthorized", ((ErrorResponse) result2).message());
    }

}
