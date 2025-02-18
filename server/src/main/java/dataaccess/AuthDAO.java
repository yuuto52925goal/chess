package dataaccess;

import model.data.UserData;

public interface AuthDAO {

    public String checkAuth(String authToken);

    public void deleteAllAuths();

    public void deleteAuth(String username);

    public String createAuth(UserData user);

    public String getUser(String token);
}
