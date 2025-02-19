package dataaccess;

import model.data.AuthData;

public interface AuthDAO {

    public String checkAuth(String authToken);

    public void deleteAllAuths();

    public void deleteAuth(String username);

    public String createAuth(String username);
}
