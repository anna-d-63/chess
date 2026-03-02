package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.UserData;
import org.junit.jupiter.api.*;
import server.requestandresult.*;
import services.UserService;

import static org.junit.jupiter.api.Assertions.*;

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
    void alreadyTakenRegister(){
        UserData goodUser = new UserData("kevin", "pwd", "kevin@email.com");
        RegisterRequest goodRequest = new RegisterRequest(goodUser.username(), goodUser.password(), goodUser.password());
        RegisterResult goodResult = service.register(goodRequest);

        assertEquals(1, userDAO.getUsers().size());
        assertEquals(1, authDAO.getAuth().size());
        assertTrue(userDAO.getUsers().containsKey("kevin"));

        RegisterRequest alreadyTakenRequest = new RegisterRequest("kevin", "pwd2", "kevin2@email.com");
        ForbiddenResponse e = assertThrows(ForbiddenResponse.class,
                ()->service.register(alreadyTakenRequest));

        assertEquals("already taken", e.getMessage());
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

    @Test
    void badLogout(){
        RegisterRequest registerReq = new RegisterRequest("Anna", "pwd", "anna@email.com");
        RegisterResult registerRes = service.register(registerReq);

        assertEquals(1, userDAO.getUsers().size());
        assertEquals(1, authDAO.getAuth().size());
        assertTrue(userDAO.getUsers().containsKey("Anna"));

        LogoutRequest logoutRequest = new LogoutRequest("fakeAuthToken");
        UnauthorizedResponse e = assertThrows(UnauthorizedResponse.class,
                ()->service.logout(logoutRequest));

        assertEquals("unauthorized", e.getMessage());
    }

    @Test
    void loginUser(){
        //register first
        RegisterRequest registerFirst = new RegisterRequest("Anna", "password", "anna@email.com");
        RegisterResult registerResult = service.register(registerFirst);

        assertEquals(1, userDAO.getUsers().size());
        assertEquals(1, authDAO.getAuth().size());
        assertTrue(userDAO.getUsers().containsKey("Anna"));

        //log them out
        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        service.logout(logoutRequest);

        assertEquals(1, userDAO.getUsers().size());
        assertEquals(0, authDAO.getAuth().size());

        //log back in
        LoginRequest logBackIn = new LoginRequest("Anna", "password");
        LoginResult result = service.login(logBackIn);

        assertEquals(1, userDAO.getUsers().size());
        assertEquals(1, authDAO.getAuth().size());
        assertNotEquals(registerResult.authToken(), result.authToken());
    }

    @Test
    void badLoginPassword(){
        //register
        RegisterRequest registerRequest = new RegisterRequest("kevin", "password", "kevin@email.com");
        RegisterResult registerResult = service.register(registerRequest);

        assertEquals(1, userDAO.getUsers().size());
        assertEquals(1, authDAO.getAuth().size());
        assertTrue(userDAO.getUsers().containsKey("kevin"));

        //logout
        var logoutReq = new LogoutRequest(registerResult.authToken());
        service.logout(logoutReq);

        assertEquals(1, userDAO.getUsers().size());
        assertEquals(0, authDAO.getAuth().size());

        //bad login
        LoginRequest badLogin = new LoginRequest("kevin", "wrongPassword");
        UnauthorizedResponse e = assertThrows(UnauthorizedResponse.class,
                ()->service.login(badLogin));

        assertEquals("unauthorized", e.getMessage());
    }
}
