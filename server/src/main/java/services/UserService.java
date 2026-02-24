package services;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import server.requestAndResult.*;

import java.util.UUID;

public class UserService {

    public RegisterResult register(RegisterRequest request) {
        String username = request.username();
        UserData userData  = db.getUser(username);
        if (userData != null){
            //403 already taken exception
            System.out.println("Already Taken");
        }
        db.createUser(username, request.password(), request.email());
        String authToken = createAuthToken();
        db.createAuth(username, authToken);
        return new RegisterResult(username, authToken);
    }

    public LoginResult login(LoginRequest request){
        String username = request.username();
        UserData userData  = db.getUser(username);
        if (userData == null){
            //401 unauthorized exception
            System.out.println("Unauthorized");
        }
        String authToken = createAuthToken();
        db.createAuth(username, authToken);
        return new LoginResult(username, authToken);
    }

    public void logout(LogoutRequest request){
        AuthData authData = db.getAuth(request.authToken());
        if (authData == null){
            //401 unauthorized exception
            System.out.println("Unauthorized");
        }
        db.deleteAuth(authData);
    }

    public String createAuthToken(){
        return UUID.randomUUID().toString();
    }
}
