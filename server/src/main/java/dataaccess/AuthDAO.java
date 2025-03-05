package dataaccess;

import model.data.AuthData;

import java.sql.SQLException;

public interface AuthDAO {

    public String checkAuth(String authToken);

    public void deleteAllAuths();

    public void deleteAuth(String username);

    public String createAuth(String username) ;
}
