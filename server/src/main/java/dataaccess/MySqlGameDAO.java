package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class MySqlGameDAO extends MySql implements GameDAO {

    public MySqlGameDAO() throws DataAccessException {
        String[] createGameStatements = {
                """
            CREATE TABLE IF NOT EXISTS auth (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `gameJson` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(gameName),
              INDEX(gameJson),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
        configureDatabase(createGameStatements);
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public void clearGames() throws DataAccessException {

    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(String playerColor, String username, int gameID) throws DataAccessException {

    }

    //get games

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
