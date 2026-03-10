package dataAccess;

import dataaccess.DataAccessException;
import dataaccess.MySqlUserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserTest {
    private final MySqlUserDAO userDAO = new MySqlUserDAO();

    public SQLUserTest() throws DataAccessException {
    }

    @BeforeEach
    void clear() throws DataAccessException{
        userDAO.clearUsers();
    }

    @Test
    void addOneUser() throws DataAccessException {
        userDAO.createUser("Anna", "password", "Anna@email.com");

        HashMap<String, UserData> expected = new HashMap<>();
        expected.put("Anna", new UserData(
                "Anna", "password", "Anna@email.com"));

        HashMap<String, UserData> actual = userDAO.getUsers();
        assertEquals(expected.size(), actual.size());
        assertTrue(expected.containsKey("Anna"));
        assertTrue(actual.containsKey("Anna"));
        assertEquals(expected.get("Anna").username(), actual.get("Anna").username());
        assertEquals(expected.get("Anna").email(), actual.get("Anna").email());
        assertEquals(expected.get("Anna").username(), actual.get("Anna").username());

        String clearPassword = expected.get("Anna").password();
        String hashedPassword = actual.get("Anna").password();
        assertTrue(BCrypt.checkpw(clearPassword, hashedPassword));
    }

    @Test
    void addManyUsers() throws DataAccessException {
        userDAO.createUser("Anna", "password", "Anna@email.com");
        userDAO.createUser("Kevin", "pwd", "kevin@email.com");
        userDAO.createUser("Eve", "pwd2", "eve@email.com");

        assertEquals(3, userDAO.getUsers().size());
    }

    @Test
    void badCreateUser() throws DataAccessException {
        DataAccessException e = assertThrows(DataAccessException.class,
                ()->userDAO.createUser(null, null, null));
    }

    @Test
    void getUserSuccess() throws DataAccessException {
        userDAO.createUser("Anna", "password", "email");

        assertEquals(1, userDAO.getUsers().size());

        UserData user = userDAO.getUser("Anna");

        assertEquals("Anna", user.username());
        assertTrue(BCrypt.checkpw("password", user.password()));
        assertEquals("email", user.email());
    }

    @Test
    void badGetUser() throws DataAccessException {
        userDAO.createUser("Kevin", "Fiance", "man@email.com");

        UserData fakeUser = userDAO.getUser("Anna");
        assertNull(fakeUser);
    }

    @Test
    void clearUsers() throws DataAccessException {
        userDAO.createUser("Susanna", "password", "susanna@email.com");
        userDAO.createUser("William", "pwd", "william@email.com");
        userDAO.createUser("Aaron", "pwd2", "aaron@email.com");

        assertEquals(3, userDAO.getUsers().size());

        userDAO.clearUsers();

        assertEquals(0, userDAO.getUsers().size());
    }
}
