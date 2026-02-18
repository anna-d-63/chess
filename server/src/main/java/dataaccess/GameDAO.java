package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void createGame(String gameName) throws DataAccessException;
    void deleteGame(int gameID) throws DataAccessException;
    void clearGames() throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(String playerColor, int gameID) throws DataAccessException;
}
