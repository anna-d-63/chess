package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.UserData;
import server.requestAndResult.*;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) {
        if (request.username() == null ||
            request.password() == null ||
            request.email() == null) {
            throw new BadRequestResponse("Error: bad request");
        }
        String username = request.username();
        UserData userData  = userDAO.getUser(username);
        if (userData != null) {
            throw new ForbiddenResponse("Error: already taken");
        }
        userDAO.createUser(username, request.password(), request.email());
        String authToken = createAuthToken();
        authDAO.createAuth(username, authToken);
        return new RegisterResult(username, authToken);
    }

    public LoginResult login(LoginRequest request){
        String username = request.username();
        UserData userData  = userDAO.getUser(username);
        if (userData == null){
            throw new BadRequestResponse();
        }
        String authToken = createAuthToken();
        authDAO.createAuth(username, authToken);
        return new LoginResult(username, authToken);
    }

    public void logout(LogoutRequest request){
        AuthData authData = authDAO.getAuth(request.authToken());
        if (authData == null){
            throw new UnauthorizedResponse();
        }
        authDAO.deleteAuth(request.authToken());
    }

    public String createAuthToken(){
        return UUID.randomUUID().toString();
    }
}
