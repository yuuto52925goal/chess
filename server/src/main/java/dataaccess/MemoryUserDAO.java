package dataaccess;

import model.data.UserData;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {

    private ArrayList<UserData> users;

    public  MemoryUserDAO(){
        users = new ArrayList<>();
    }

    @Override
    public UserData getUser(String username) {
        for (UserData user : users) {
            if (Objects.equals(user.username(), username)){
                return user;
            }
        }
        return null;
    }

    @Override
    public UserData createUser(UserData user) {
        users.add(user);
        return user;
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }
}
