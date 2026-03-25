package client;

import exceptions.DataAccessException;
import org.junit.jupiter.api.*;
import requestandresult.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


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
    public void badRegister() {
        var badRequest = new RegisterRequest("Anna", "password", null);
        assertThrows(DataAccessException.class, ()->facade.register(badRequest));
    }

    @Test
    public void logout() throws DataAccessException {
        var request = new RegisterRequest("Anna", "password", "anna@email.com");
        RegisterResult result = facade.register(request);

        assertNotNull(result);
        assertEquals("Anna", result.username());
        assertNotNull(result.authToken());

        var logoutRequest = new LogoutRequest(result.authToken());
        facade.logout(logoutRequest);
    }

    @Test
    public void badLogout() {
        var logoutRequest = new LogoutRequest("badAuth");
        assertThrows(DataAccessException.class, ()->facade.logout(logoutRequest));
    }

    @Test
    public void login() throws DataAccessException {
        var registerRequest = new RegisterRequest("Anna", "pwd", "anna@email.com");
        RegisterResult registerResult = facade.register(registerRequest);

        var logoutRequest = new LogoutRequest(registerResult.authToken());
        facade.logout(logoutRequest);

        var loginRequest = new LoginRequest("Anna", "pwd");
        LoginResult loginResult = facade.login(loginRequest);

        assertEquals("Anna", loginResult.username());
        assertNotNull(loginResult.authToken());
    }

    @Test
    public void badLogin() {
        var badLoginReq = new LoginRequest("anna", "password");
        assertThrows(DataAccessException.class, ()-> facade.login(badLoginReq));
    }

    @Test
    public void createGame() throws DataAccessException {
        var registerRequest = new RegisterRequest("Anna", "pwd", "anna@email.com");
        RegisterResult registerResult = facade.register(registerRequest);

        var createGameRequest = new CreateGameRequest(registerResult.authToken(), "game1");
        CreateGameResult createGameResult = facade.createGame(createGameRequest);

        assertNotNull(createGameResult.gameID());
    }

    @Test
    public void badCreate() throws DataAccessException {
        var registerRequest = new RegisterRequest("Anna", "pwd", "anna@email.com");
        RegisterResult registerResult = facade.register(registerRequest);

        var badCreateReq = new CreateGameRequest("badAuth", "game1");
        assertThrows(DataAccessException.class, ()-> facade.createGame(badCreateReq));
    }

    @Test
    public void joinGame() throws DataAccessException {
        var registerRequest = new RegisterRequest("Anna", "pwd", "anna@email.com");
        RegisterResult registerResult = facade.register(registerRequest);

        var createGameRequest = new CreateGameRequest(registerResult.authToken(), "game1");
        CreateGameResult createGameResult = facade.createGame(createGameRequest);

        var joinGameRequest = new JoinGameRequest(
                registerResult.authToken(), "WHITE", createGameResult.gameID());
        facade.joinGame(joinGameRequest);

        assertNotNull(createGameResult);
        assertNotNull(registerResult);
    }

    @Test
    public void badJoin() throws DataAccessException {
        var registerRequest = new RegisterRequest("Anna", "pwd", "anna@email.com");
        RegisterResult registerResult = facade.register(registerRequest);

        var joinGameRequest = new JoinGameRequest(
                registerResult.authToken(), "WHITE", 2);
        assertThrows(DataAccessException.class, ()-> facade.joinGame(joinGameRequest));
    }

    @Test
    public void listGames() throws DataAccessException {
        var registerRequest = new RegisterRequest("Anna", "pwd", "anna@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        var authToken = registerResult.authToken();

        var createGameRequest1 = new CreateGameRequest(authToken, "game1");
        facade.createGame(createGameRequest1);

        var createGameRequest2 = new CreateGameRequest(authToken, "game2");
        facade.createGame(createGameRequest2);

        var createGameRequest3 = new CreateGameRequest(authToken, "game3");
        facade.createGame(createGameRequest3);

        var listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult = facade.listGames(listGamesRequest);

        assertNotNull(listGamesResult.games());
    }

    @Test
    public void badList() throws DataAccessException {
        var registerRequest = new RegisterRequest("Anna", "pwd", "anna@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        var authToken = registerResult.authToken();

        var createGameRequest1 = new CreateGameRequest(authToken, "game1");
        facade.createGame(createGameRequest1);

        var badListReq = new ListGamesRequest("badAuth");
        assertThrows(DataAccessException.class, ()-> facade.listGames(badListReq));
    }

    @Test
    public void clear() throws DataAccessException {
        var registerRequest = new RegisterRequest("Anna", "pwd", "anna@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        var authToken = registerResult.authToken();

        var createGameRequest1 = new CreateGameRequest(authToken, "game1");
        facade.createGame(createGameRequest1);

        facade.clear();

        assertNotNull(registerResult);
    }
}
