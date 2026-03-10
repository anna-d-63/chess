package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import server.requestandresult.*;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        checkIfNull(request);
        String username = request.username();
        UserData userData  = userDAO.getUser(username);
        if (userData != null) {
            throw new ForbiddenResponse("already taken");
        }
        userDAO.createUser(username, request.password(), request.email());
        String authToken = createAuthToken();
        authDAO.createAuth(username, authToken);
        return new RegisterResult(username, authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        checkIfNull(request);
        String username = request.username();
        UserData userData  = userDAO.getUser(username);
        checkIfAuthorized(userData);
        if (!verifyUser(username, request.password(), userData.password())){ //!request.password().equals(userData.password())
            throw new UnauthorizedResponse("unauthorized");
        }
        String authToken = createAuthToken();
        authDAO.createAuth(username, authToken);
        return new LoginResult(username, authToken);
    }

    public void logout(LogoutRequest request) throws DataAccessException {
        checkIfNull(request);
        AuthData authData = authDAO.getAuth(request.authToken());
        checkIfAuthorized(authData);
        authDAO.deleteAuth(request.authToken());
    }

    private boolean verifyUser(String username, String providedClearTextPassword, String hashedPassword) {
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    private String createAuthToken(){
        return UUID.randomUUID().toString();
    }

    private void checkIfNull(ParentRequest request) {
        if (request.hasNullFields()){throw new BadRequestResponse("bad request");}
    }

    private void checkIfAuthorized(Object data){
        if (data == null) {
            throw new UnauthorizedResponse("unauthorized");}
    }
}
