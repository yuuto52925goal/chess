package service;

import dataaccess.*;
import model.data.UserData;
import model.request.LoginRequest;
import model.request.LogoutRequest;
import model.request.RegisterRequest;
import model.result.ErrorResponse;
import model.result.LoginResult;
import model.result.LogoutResult;
import model.result.RegisterResult;
import org.mindrot.jbcrypt.BCrypt;


public class UserService {

    private AuthDAO authAccess;
    private UserDAO userAccess;

    public UserService() {
        authAccess = new MysqlAuthDAO();
        userAccess = new MysqlUserDAO();
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

    public Object login(LoginRequest loginRequest)  {
        String username = loginRequest.username();
        String password = loginRequest.password();
        if (userAccess.getUser(username) != null) {
            String passwordOrigin = userAccess.getUser(username).password();
            if (passwordOrigin.startsWith("$2a$") || passwordOrigin.startsWith("$2b$")|| passwordOrigin.startsWith("$2y$")) {
                if (!BCrypt.checkpw(password, passwordOrigin)) {
                    return new ErrorResponse("Error: unauthorized");
                }
            }else if (!passwordOrigin.equals(password)){
                return new ErrorResponse("Error: unauthorized");
            }
        }else{
            return new ErrorResponse("Error: unauthorized");
        }
        return new LoginResult(username, authAccess.createAuth(username));
    }

    public Object logout(LogoutRequest logoutRequest)  {
        String token = logoutRequest.token();
        String username = authAccess.checkAuth(token);
        if (username == null) {
            return new ErrorResponse("Error: unauthorized");
        }
        authAccess.deleteAuth(token);
        return new LogoutResult();
    }

    public String authCheck(String token)  {
        return authAccess.checkAuth(token);
    }

    public void userAuthClear(){
        authAccess.deleteAllAuths();
        userAccess.deleteAllUsers();
    }
}
