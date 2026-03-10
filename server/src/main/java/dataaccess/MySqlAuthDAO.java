package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class MySqlAuthDAO extends MySql implements AuthDAO {

    public MySqlAuthDAO() throws DataAccessException {
        String[] createAuthStatements = {
                """
            CREATE TABLE IF NOT EXISTS auth (
              `id` int NOT NULL AUTO_INCREMENT,
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(authToken),
              INDEX(name),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
        configureDatabase(createAuthStatements);
    }

    @Override
    public void createAuth(String username, String authToken) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) values (?, ?)";
        int id = executeUpdate(statement, authToken, username);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auth WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var user = rs.getString("username");
                        var auth = rs.getString("authToken");

                        return new AuthData(auth, user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("failed to get auth", e);
        }
        return null;
    }

    @Override
    public void clearAuth() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    public HashMap<String, AuthData> getAuth() throws DataAccessException {
        HashMap<String, AuthData> auths = new HashMap<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * from auth";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()){
                        var username = rs.getString("username");
                        var authToken = rs.getString("authToken");

                        auths.put(authToken, new AuthData(authToken, username));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("failed to get users", e);
        }
        return auths;
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
