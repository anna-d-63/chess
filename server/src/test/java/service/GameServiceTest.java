package service;

import Exceptions.DataAccessException;
import dataaccess.*;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import requestandresult.*;
import services.GameService;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private final MySqlAuthDAO authDAO = new MySqlAuthDAO();
    private final MySqlGameDAO gameDAO = new MySqlGameDAO();

    private final GameService service = new GameService(authDAO, gameDAO);

    public GameServiceTest() throws DataAccessException {
    }

    @BeforeEach
    void clear() throws DataAccessException{
        gameDAO.clearGames();
        authDAO.clearAuth();

        authDAO.createAuth("user", "authToken");
    }

    @Test
    void createNewGame() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("authToken", "gameName");
        CreateGameResult result = service.createGame(request);

        assertEquals(1, authDAO.getAuths().size());
        assertEquals(1, gameDAO.getGames().size());
    }

    @Test
    void badCreateGame() throws DataAccessException {
        CreateGameRequest createGameRequest = new CreateGameRequest("badAuthToken", "gameName");
        UnauthorizedResponse e = assertThrows(UnauthorizedResponse.class,
                ()->service.createGame(createGameRequest));

        assertEquals("unauthorized", e.getMessage());
    }

    @Test
    void joinAGame() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("authToken", "newGame");
        CreateGameResult result = service.createGame(request);

        var joinGameRequest = new JoinGameRequest("authToken", "WHITE", result.gameID());
        service.joinGame(joinGameRequest);

        assertEquals("user", gameDAO.getGames().get(result.gameID()).whiteUsername());

    }

    @Test
    void joinGameWrongID() throws DataAccessException {
        CreateGameRequest makeGame = new CreateGameRequest("authToken", "gameName");
        CreateGameResult result = service.createGame(makeGame);

        JoinGameRequest wrongGameID = new JoinGameRequest("authToken", "WHITE", 1000);
        BadRequestResponse e = assertThrows(BadRequestResponse.class,
                ()->service.joinGame(wrongGameID));

        assertEquals("bad request", e.getMessage());
    }

    @Test
    void joinGameUserTaken() throws DataAccessException {
        CreateGameRequest newGame = new CreateGameRequest("authToken", "gameName");
        CreateGameResult result = service.createGame(newGame);

        JoinGameRequest goodJoin = new JoinGameRequest("authToken", "WHITE", result.gameID());
        service.joinGame(goodJoin);

        String actualWhiteUser = gameDAO.getGame(result.gameID()).whiteUsername();
        assertEquals("user", actualWhiteUser);

        authDAO.createAuth("badUser", "authToken2");
        JoinGameRequest userTakenJoin = new JoinGameRequest("authToken2", "WHITE", result.gameID());
        ForbiddenResponse e = assertThrows(ForbiddenResponse.class,
                ()-> service.joinGame(userTakenJoin));
        assertEquals("already taken", e.getMessage());
    }

    @Test
    void listAllGames() throws DataAccessException {
        var game1 = new CreateGameRequest("authToken", "game1");
        CreateGameResult game1result = service.createGame(game1);

        var game2 = new CreateGameRequest("authToken", "game2");
        CreateGameResult game2result = service.createGame(game2);

        assertEquals(2, gameDAO.getGames().size());

        ListGamesRequest listGamesRequest = new ListGamesRequest("authToken");
        ListGamesResult listGamesResult = service.listGames(listGamesRequest);

        assertEquals(2, listGamesResult.games().size());
    }

    @Test
    void badListGames() throws DataAccessException {
        var newGame = new CreateGameRequest("authToken", "gameName");
        CreateGameResult result = service.createGame(newGame);

        ListGamesRequest listGamesRequest = new ListGamesRequest("badAuthToken");
        UnauthorizedResponse e = assertThrows(UnauthorizedResponse.class,
                ()->service.listGames(listGamesRequest));

        assertEquals("unauthorized", e.getMessage());
    }
}
