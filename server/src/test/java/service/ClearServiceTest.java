package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.Test;
import server.requestandresult.CreateGameRequest;
import server.requestandresult.CreateGameResult;
import server.requestandresult.RegisterRequest;
import server.requestandresult.RegisterResult;
import services.ClearService;
import services.GameService;
import services.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClearServiceTest {
    private final MemoryUserDAO userDAO = new MemoryUserDAO();
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final UserService userService = new UserService(userDAO, authDAO);
    private final GameService gameService = new GameService(authDAO, gameDAO);
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

    @Test
    void buildDatabaseAndClear() throws DataAccessException {
        //2 users
        var user1 = new RegisterRequest("Anna", "password", "anna@email.com");
        RegisterResult user1result = userService.register(user1);

        var user2 = new RegisterRequest("kevin", "pwd", "kevin@email.com");
        RegisterResult user2result = userService.register(user2);

        assertEquals(2, userDAO.getUsers().size());
        assertEquals(2, authDAO.getAuth().size());

        //2 games
        var game1 = new CreateGameRequest(user1result.authToken(), "game1");
        CreateGameResult game1result = gameService.createGame(game1);

        var game2 = new CreateGameRequest(user2result.authToken(), "game2");
        CreateGameResult game2result = gameService.createGame(game2);

        assertEquals(2, gameDAO.getGames().size());

        //clear
        clearService.clear();

        assertEquals(0, userDAO.getUsers().size());
        assertEquals(0, authDAO.getAuth().size());
        assertEquals(0, gameDAO.getGames().size());
    }
}
