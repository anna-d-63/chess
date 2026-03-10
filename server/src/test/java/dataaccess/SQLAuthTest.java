package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthTest {
    private final MySqlAuthDAO authDAO = new MySqlAuthDAO();

    public SQLAuthTest() throws DataAccessException {
    }

    @BeforeEach
    void clear() throws DataAccessException {
        authDAO.clearAuth();
    }

    @Test
    void addAuthorizationData() throws DataAccessException {
        authDAO.createAuth("Anna", "authToken");

        HashMap<String, AuthData> auths = authDAO.getAuths();

        assertEquals(1, auths.size());
        assertEquals("Anna", auths.get("authToken").username());
    }

    @Test
    void addLotsOfAuth() throws DataAccessException {
        authDAO.createAuth("Anna", "authToken1");
        authDAO.createAuth("Kevin", "authToken2");
        authDAO.createAuth("Eve", "authToken3");

        HashMap<String, AuthData> auths = authDAO.getAuths();

        assertEquals(3, auths.size());
    }

    @Test
    void failToAddAuth() {
        DataAccessException e = assertThrows(DataAccessException.class,
                ()->authDAO.createAuth(null, null));
    }

    @Test
    void deleteAuthRow() throws DataAccessException {
        authDAO.createAuth("Anna", "authToken");
        authDAO.createAuth("Kevin", "authToken1");

        assertEquals(2, authDAO.getAuths().size());

        authDAO.deleteAuth("authToken");

        assertEquals(1, authDAO.getAuths().size());
        assertEquals(new AuthData("authToken1", "Kevin"), authDAO.getAuths().get("authToken1"));
    }

    @Test
    void cantDeleteSomethingNotThere() throws DataAccessException {
        authDAO.createAuth("Anna", "authToken");

        assertEquals(1, authDAO.getAuths().size());

        authDAO.deleteAuth("notAnAuthToken");

        assertEquals(1, authDAO.getAuths().size());
    }

    @Test
    void getAuthOfMany() throws DataAccessException {
        authDAO.createAuth("William", "at1");
        authDAO.createAuth("Susie", "at2");
        authDAO.createAuth("Aaron", "at3");
        authDAO.createAuth("Bryce", "at4");

        AuthData aaron = authDAO.getAuth("at3");

        assertEquals(4, authDAO.getAuths().size());
        assertEquals("Aaron", aaron.username());
        assertEquals("at3", aaron.authToken());
    }

    @Test
    void getAuthNotThere() throws DataAccessException {
        authDAO.createAuth("Anna", "at");
        AuthData authData = authDAO.getAuth("authToken");

        assertNull(authData);
    }

    @Test
    void clearAuth() throws DataAccessException {
        authDAO.createAuth("a", "at1");
        authDAO.createAuth("b", "at2");
        authDAO.createAuth("c", "at3");

        assertEquals(3, authDAO.getAuths().size());

        authDAO.clearAuth();

        assertEquals(0, authDAO.getAuths().size());
    }
}
