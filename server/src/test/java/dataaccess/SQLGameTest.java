package dataaccess;

import exceptions.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameTest {

    private final MySqlGameDAO gameDAO = new MySqlGameDAO();

    public SQLGameTest() throws DataAccessException {
    }

    @BeforeEach
    void clear() throws DataAccessException {
        gameDAO.clearGames();
    }

    @Test
    void addOneGame() throws DataAccessException {
        GameData gameData = gameDAO.createGame("Anna's Game");

        assertEquals(1, gameDAO.getGames().size());
        assertEquals("Anna's Game", gameData.gameName());
        assertNotEquals(0, gameData.gameID());
        assertNull(gameData.whiteUsername());
        assertNull(gameData.blackUsername());
    }

    @Test
    void addMoreGames() throws DataAccessException {
        gameDAO.createGame("a");
        gameDAO.createGame("b");
        gameDAO.createGame("c");

        assertEquals(3, gameDAO.getGames().size());
    }

    @Test
    void addBadGame() {
        DataAccessException e = assertThrows(DataAccessException.class,
                ()->gameDAO.createGame(null));
    }

    @Test
    void getGameOfMany() throws DataAccessException {
        gameDAO.createGame("One");
        GameData game2 = gameDAO.createGame("Two");
        gameDAO.createGame("Three");

        GameData wantedGame = gameDAO.getGame(game2.gameID());

        assertEquals("Two", wantedGame.gameName());
    }

    @Test
    void cantGetGameThatDoesntExist() throws DataAccessException {
        GameData notThere = gameDAO.getGame(3000);

        assertNull(notThere);
    }

    @Test
    void listAllGames() throws DataAccessException {
        GameData one = gameDAO.createGame("first");
        GameData two = gameDAO.createGame("second");
        GameData three = gameDAO.createGame("third");

        Collection<GameData> gamesList = gameDAO.listGames();

        assertEquals(3, gamesList.size());
        assertTrue(gamesList.contains(one));
        assertTrue(gamesList.contains(two));
        assertTrue(gamesList.contains(three));
    }

    @Test
    void listNoGames() throws DataAccessException {
        Collection<GameData> gamesList = gameDAO.listGames();

        assertEquals(0, gamesList.size());
    }

    @Test
    void validJoinGame() throws DataAccessException {
        GameData gameData = gameDAO.createGame("Anna's Game");
        gameDAO.updateGame("BLACK", "Anna", gameData.gameID());

        GameData updatedGame = gameDAO.getGame(gameData.gameID());

        assertEquals("Anna", updatedGame.blackUsername());
        assertNull(updatedGame.whiteUsername());
    }

    @Test
    void cantJoinNonExistentGame() throws DataAccessException {
        gameDAO.createGame("hi");
        gameDAO.createGame("bye");

        Collection<GameData> expected = gameDAO.listGames();
        gameDAO.updateGame("WHITE", "Anna", 0);

        Collection<GameData> actual = gameDAO.listGames();

        assertEquals(expected, actual);
    }

    @Test
    void clearAllGames() throws DataAccessException {
        gameDAO.createGame("a");
        gameDAO.createGame("b");

        assertEquals(2, gameDAO.getGames().size());

        gameDAO.clearGames();

        assertEquals(0, gameDAO.getGames().size());
    }
}
