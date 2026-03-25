package dataaccess;

import exceptions.DataAccessException;
import model.UserData;

public interface UserDAO {
    void createUser(String username, String password, String email) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clearUsers() throws DataAccessException;
}
