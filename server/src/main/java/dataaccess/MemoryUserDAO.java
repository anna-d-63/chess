package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(String username, String password, String email) {
        UserData userData = new UserData(username, password, email);
        users.put(username, userData);
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void clearUsers() {
        users.clear();
    }

    @Override
    public String toString() {
        return "MemoryUserDAO{" +
                "users=" + users +
                '}';
    }
}
