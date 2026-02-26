package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(String username, String authToken);
    void deleteAuth(String authToken);
    AuthData getAuth(String authToken);
    void clearAuth();
}
