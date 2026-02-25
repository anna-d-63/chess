package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.*;
import server.requestAndResult.RegisterRequest;
import server.requestAndResult.RegisterResult;
import services.UserService;

public class UserServiceTest {
    private final MemoryUserDAO userDAO = new MemoryUserDAO();
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private UserService service = new UserService(userDAO, authDAO);

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

        System.out.println(request);
        System.out.println(result);
    }
}
