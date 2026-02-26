package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;
import server.requestandresult.LogoutRequest;
import server.requestandresult.RegisterRequest;
import server.requestandresult.RegisterResult;
import services.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserServiceTest {
    private final MemoryUserDAO userDAO = new MemoryUserDAO();
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final UserService service = new UserService(userDAO, authDAO);

    @BeforeEach
    void clear() {
        userDAO.clearUsers();
        authDAO.clearAuth();
    }

    @Test
    void registerNewUser(){
        UserData user = new UserData("Anna", "password", "anna@email.com");
        RegisterRequest request = new RegisterRequest(user.username(), user.password(), user.email());
        RegisterResult result = service.register(request);

        assertEquals(1, userDAO.getUsers().size());
        assertEquals(1, authDAO.getAuth().size());
        assertTrue(userDAO.getUsers().containsKey("Anna"));
    }

    @Test
    void logoutUser(){
        UserData user = new UserData("Anna", "password", "anna@email.com");
        RegisterRequest request = new RegisterRequest(user.username(), user.password(), user.email());
        RegisterResult result = service.register(request);

        assertEquals(1, userDAO.getUsers().size());
        assertEquals(1, authDAO.getAuth().size());
        assertTrue(userDAO.getUsers().containsKey("Anna"));

        LogoutRequest logoutRequest = new LogoutRequest(result.authToken());
        service.logout(logoutRequest);

        assertEquals(1, userDAO.getUsers().size());
        assertEquals(0, authDAO.getAuth().size());
        assertTrue(userDAO.getUsers().containsKey("Anna"));
    }
}
