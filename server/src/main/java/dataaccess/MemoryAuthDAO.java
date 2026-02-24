package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> auth = new HashMap<>();

    @Override
    public void createAuth(String username, String authToken) { //throws DataAccessException {
        AuthData authData = new AuthData(authToken, username);
        auth.put(authToken, authData);
    }

    @Override
    public void deleteAuth(String authToken) { //throws DataAccessException {
        auth.remove(authToken);
    }

    @Override
    public AuthData getAuth(String authToken) { //throws DataAccessException {
        return auth.get(authToken);
    }

    @Override
    public void clearAuth() { //throws DataAccessException {
        auth.clear();
    }
}
