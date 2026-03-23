package client;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;
import server.requestandresult.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void clearDB() throws DataAccessException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void register() throws DataAccessException {
        var request = new RegisterRequest("Anna", "password", "anna@email.com");
        RegisterResult res = facade.register(request);

        assertNotNull(res);
        assertEquals("Anna", res.username());
        assertNotNull(res.authToken());
    }

    @Test
    public void logout() throws DataAccessException {
        var request = new RegisterRequest("Anna", "password", "anna@email.com");
        RegisterResult result = facade.register(request);

        assertNotNull(result);
        assertEquals("Anna", result.username());
        assertNotNull(result.authToken());

        var logoutRequest = new LogoutRequest(result.authToken());
        facade.logout(logoutRequest, result.authToken());
    }

    @Test
    public void login() throws DataAccessException {
        var registerRequest = new RegisterRequest("Anna", "pwd", "anna@email.com");
        RegisterResult registerResult = facade.register(registerRequest);

        var logoutRequest = new LogoutRequest(registerResult.authToken());
        facade.logout(logoutRequest, registerResult.authToken());

        var loginRequest = new LoginRequest("Anna", "pwd");
        LoginResult loginResult = facade.login(loginRequest);

        assertEquals("Anna", loginResult.username());
        assertNotNull(loginResult.authToken());
    }

    @Test
    public void createGame() throws DataAccessException {
        var registerRequest = new RegisterRequest("Anna", "pwd", "anna@email.com");
        RegisterResult registerResult = facade.register(registerRequest);

        var createGameRequest = new CreateGameRequest(registerResult.authToken(), "game1");
        CreateGameResult createGameResult = facade.createGame(createGameRequest, registerResult.authToken());

        assertNotNull(createGameResult.gameID());
    }

    @Test
    public void joinGame() throws DataAccessException {
        var registerRequest = new RegisterRequest("Anna", "pwd", "anna@email.com");
        RegisterResult registerResult = facade.register(registerRequest);

        var createGameRequest = new CreateGameRequest(registerResult.authToken(), "game1");
        CreateGameResult createGameResult = facade.createGame(createGameRequest, registerResult.authToken());

        var joinGameRequest = new JoinGameRequest(
                registerResult.authToken(), "WHITE", createGameResult.gameID());
        facade.joinGame(joinGameRequest, registerResult.authToken());
    }

    @Test
    public void listGames() throws DataAccessException {
        var registerRequest = new RegisterRequest("Anna", "pwd", "anna@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        var authToken = registerResult.authToken();

        var createGameRequest1 = new CreateGameRequest(authToken, "game1");
        facade.createGame(createGameRequest1, authToken);

        var createGameRequest2 = new CreateGameRequest(authToken, "game2");
        facade.createGame(createGameRequest2, authToken);

        var createGameRequest3 = new CreateGameRequest(authToken, "game3");
        facade.createGame(createGameRequest3, authToken);

        var listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult = facade.listGames(listGamesRequest, authToken);

        assertNotNull(listGamesResult);
    }
}
