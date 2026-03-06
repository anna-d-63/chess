package dataaccess;

import model.GameData;

import javax.xml.crypto.Data;
import java.util.Collection;

public interface GameDAO {
    GameData createGame(String gameName) throws DataAccessException;
    void clearGames() throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(String playerColor, String username, int gameID) throws DataAccessException;
}
