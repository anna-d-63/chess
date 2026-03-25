package dataaccess;

import Exceptions.DataAccessException;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

public class MySqlGameDAO extends MySql implements GameDAO {

    Gson json = new Gson();

    public MySqlGameDAO() throws DataAccessException {
        String[] createGameStatements = {
                """
            CREATE TABLE IF NOT EXISTS games (
              `gameID` int NOT NULL,
              `whiteUsername` varchar(256) DEFAULT NULL,
              `blackUsername` varchar(256) DEFAULT NULL,
              `gameName` varchar(256) NOT NULL,
              `gameJson` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(gameName)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
        configureDatabase(createGameStatements);
    }

    @Override
    public GameData createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO games (gameID, gameName, gameJson) VALUES (?, ?, ?)";
        Random random = new Random();
        boolean inserted = false;
        var game = new ChessGame();
        String gameJson = json.toJson(game);
        int id = 0;
        try (Connection conn = DatabaseManager.getConnection()) {
            while (!inserted) {
                id = 1000 + random.nextInt(9000);
                try (PreparedStatement ps = conn.prepareStatement(statement)){
                    ps.setInt(1, id);
                    ps.setString(2, gameName);
                    ps.setString(3, gameJson);

                    ps.executeUpdate();
                    inserted = true;
                } catch (SQLException e) {
                    if (e.getErrorCode() == 1062) {
                        continue;
                    } else {
                        throw e;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return new GameData(id, null, null, gameName, game);
    }

    @Override
    public void clearGames() throws DataAccessException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        var gamesList = new ArrayList<GameData>();
        try (Connection conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        gamesList.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("failed to list games", e);
        }
        return gamesList;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("failed to get game", e);
        }
        return null;
    }

    @Override
    public void updateGame(String playerColor, String username, int gameID) throws DataAccessException {
        GameData oldGame = getGame(gameID);
        GameData updatedGame;
        try (Connection conn = DatabaseManager.getConnection()) {
            if (playerColor.equals("WHITE")) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE games SET whiteUsername=? WHERE gameID=?")) {
                    ps.setString(1, username);
                    ps.setInt(2, gameID);

                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE games SET blackUsername=? WHERE gameID=?")) {
                    ps.setString(1, username);
                    ps.setInt(2, gameID);

                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("failed to update game", e);
        }
    }

    public HashMap<Integer, GameData> getGames() throws DataAccessException {
        HashMap<Integer, GameData> games = new HashMap<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * from games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()){
                        var gameID = rs.getInt("gameID");
                        GameData gameData = readGame(rs);

                        games.put(gameID, gameData);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("failed to get users", e);
        }
        return games;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var wUsername = rs.getString("whiteUsername");
        var bUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var gameJson = rs.getString("gameJson");
        ChessGame game = json.fromJson(gameJson, ChessGame.class);
        return new GameData(gameID, wUsername, bUsername, gameName, game);
    }

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
