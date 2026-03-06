package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Objects;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> auth = new HashMap<>();

    @Override
    public void createAuth(String username, String authToken) {
        AuthData authData = new AuthData(authToken, username);
        auth.put(authToken, authData);
    }

    @Override
    public void deleteAuth(String authToken) {
        auth.remove(authToken);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return auth.get(authToken);
    }

    @Override
    public void clearAuth() {
        auth.clear();
    }

    @Override
    public String toString() {
        return "MemoryAuthDAO{" +
                "users=" + auth +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryAuthDAO that = (MemoryAuthDAO) o;
        return Objects.equals(auth, that.auth);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(auth);
    }

    public HashMap<String, AuthData> getAuth() {
        return auth;
    }
}
