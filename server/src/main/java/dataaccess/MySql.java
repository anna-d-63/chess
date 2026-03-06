package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class MySql {
    public void configureDatabase(String[] createStatements) throws Exception {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new Exception(ex.getMessage());
        }
    }
}
