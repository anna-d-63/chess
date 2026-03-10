package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
//        try (Connection conn = DatabaseManager.getConnection()) {
//            var statement = "SELECT * FROM auth WHERE authToken=?";
//            try (PreparedStatement ps = conn.prepareStatement(statement)) {
//                ps.setString(1, authToken);
//                try (ResultSet)
//            }
//        }
        return null;
    }

    @Override
    public void clearAuth() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    //get Auth

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
