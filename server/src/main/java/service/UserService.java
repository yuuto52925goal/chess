package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.data.UserData;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.result.ErrorResponse;
import model.result.LoginResult;
import model.result.LogoutResult;
import model.result.RegisterResult;

public class UserService {

    private AuthDAO authAccess;
    private UserDAO userAccess;

    public UserService() {
        authAccess = new MemoryAuthDAO();
        userAccess = new MemoryUserDAO();
    }

    public Object register(RegisterRequest registerRequest) {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        if (username == null || password == null || email == null) {
            return new ErrorResponse("Error: bad request");
        }

        if (userAccess.getUser(username) != null) {
            return new ErrorResponse("Error: already taken");
        }

        UserData newUser = new UserData(username, password, email);
        userAccess.createUser(newUser);
        String newToken = authAccess.createAuth(username);

        return new RegisterResult(username, newToken);
    }

    public Object login(LoginRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();

        if (userAccess.getUser(username) != null || userAccess.getUser(username).password().equals(password)) {
            return new ErrorResponse("Error: unauthorized");
        }

        return new LoginResult(username, authAccess.checkAuth(username));
    }

    public Object logout(LogoutRequest logoutRequest) {
        String token = logoutRequest.token();
        UserData user = userAccess.getUser(token);
        if (user == null) {
            return new ErrorResponse("Error: unauthorized");
        }
        authAccess.deleteAuth(user.username());
        return new LogoutResult(token);
    }
}
