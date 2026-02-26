package service;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.requestandresult.CreateGameRequest;
import server.requestandresult.CreateGameResult;
import server.requestandresult.JoinGameRequest;
import services.GameService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameServiceTest {

    private final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final GameService service = new GameService(authDAO, gameDAO);

    @BeforeEach
    void clear() {
        gameDAO.clearGames();
        authDAO.clearAuth();

        authDAO.createAuth("user", "authToken");
    }

    @Test
    void createNewGame(){
        CreateGameRequest request = new CreateGameRequest("authToken", "gameName");
        CreateGameResult result = service.createGame(request);

        assertEquals(1, authDAO.getAuth().size());
        assertEquals(1, gameDAO.getGames().size());
    }

    @Test
    void joinAGame(){
        CreateGameRequest request = new CreateGameRequest("authToken", "newGame");
        CreateGameResult result = service.createGame(request);

        var joinGameRequest = new JoinGameRequest("authToken", "WHITE", result.gameID());
        service.joinGame(joinGameRequest);

        System.out.println(authDAO);
        System.out.println(gameDAO);

    }
}
