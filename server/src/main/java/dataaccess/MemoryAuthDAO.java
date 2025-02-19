package dataaccess;

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
    public void deleteAuth(String username) {
        for (String token : authUsers.keySet()) {
            if (authUsers.get(token).equals(username)) {
                authUsers.remove(token);
                return;
            }
        }
    }

    @Override
    public String createAuth(UserData user) {
        String authToken = generateToken();
        authUsers.put(authToken, user.username());
        return authToken;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
