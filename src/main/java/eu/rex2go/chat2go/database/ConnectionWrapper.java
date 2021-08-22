package eu.rex2go.chat2go.database;

import eu.rex2go.chat2go.Chat2Go;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class ConnectionWrapper {

    @Getter
    private Connection connection;

    ConnectionWrapper(Connection connection) {
        this.connection = connection;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException exception) {
            Chat2Go.getInstance().getLogger().log(
                    Level.SEVERE,
                    "Could not close database connection: " + exception.getMessage());
        }
    }

    public PreparedStatement prepareStatement(String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException exception) {
            Chat2Go.getInstance().getLogger().log(
                    Level.SEVERE,
                    "Could not prepare sql statement: " + exception.getMessage());
        }

        return null;
    }

}
