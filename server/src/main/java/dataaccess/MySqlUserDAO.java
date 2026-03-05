package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO {

    @Override
    public void createUser(String username, String password, String email) {

    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void clearUsers() {

    }

    //getUsers

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

    private final String[] createUserStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws Exception {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createUserStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new Exception(ex.getMessage());
        }
    }
}
