package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(String username, String password, String email);
    UserData getUser(String username);
    void clearUsers();
}
