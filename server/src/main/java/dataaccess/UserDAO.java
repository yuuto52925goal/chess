package dataaccess;

import model.data.UserData;

public interface UserDAO {

    public UserData getUser(String username);

    public UserData createUser(UserData user);

    public void deleteAllUsers();

}
