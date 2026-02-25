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
    static UserService service = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

    @BeforeEach
    void clear() {
        service = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());
    }

    @Test
    void registerNewUser(){
        UserData user = new UserData("Anna", "password", "anna@email.com");
        RegisterRequest request = new RegisterRequest(user.username(), user.password(), user.email());
        RegisterResult result = service.register(request);


    }
}
