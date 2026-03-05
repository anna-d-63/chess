package dataaccess;

import model.AuthData;

public class MySqlAuthDAO implements AuthDAO {

    @Override
    public void createAuth(String username, String authToken) {

    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void clearAuth() {

    }

    //get Auth

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
