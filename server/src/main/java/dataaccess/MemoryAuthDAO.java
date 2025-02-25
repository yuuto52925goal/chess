package dataaccess;

import model.data.AuthData;
import model.data.UserData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    private HashMap<String, String> authUsers;

    public MemoryAuthDAO() {
        authUsers = new HashMap<>();
    }

    @Override
    public String checkAuth(String authToken) {
        return authUsers.get(authToken);
    }

    @Override
    public void deleteAllAuths() {
        authUsers.clear();
    }

    @Override
    public void deleteAuth(String deleteToken) {
        for (String token : authUsers.keySet()) {
            if (token.equals(deleteToken)) {
                authUsers.remove(token);
                return;
            }
        }
    }

    @Override
    public String createAuth(String username) {
        String authToken = generateToken();
        authUsers.put(authToken, username);
        return authToken;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
