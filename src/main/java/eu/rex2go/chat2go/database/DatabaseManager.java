package eu.rex2go.chat2go.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.rex2go.chat2go.Chat2Go;
import lombok.Getter;

import javax.annotation.Nullable;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

@Getter
public class DatabaseManager {

    private String host, database, user, password;

    private int port;

    private File file;

    private HikariConfig config;

    private HikariDataSource dataSource;

    public DatabaseManager(File file) {
        this.file = file;

        this.initSQLiteHikari();
    }

    public DatabaseManager(String host, String database, String user, String password, int port) {
        this.host = host;
        this.database = database;
        this.user = user;
        this.password = password;
        this.port = port;

        this.initMySQLHikari();
    }

    private void initSQLiteHikari() {
        config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath());

        dataSource = new HikariDataSource(config);
    }

    private void initMySQLHikari() {
        config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(user);
        config.setPassword(password);

        initConnectionProperties();

        dataSource = new HikariDataSource(config);
    }

    private void initConnectionProperties() {
        config.addDataSourceProperty("cachePrepStmts", "true"); // cache prepared statements
        config.addDataSourceProperty("prepStmtCacheSize", "250"); // amount of prepared statements to cache
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048"); // maximum limit of prepared statement length in the cache
        config.addDataSourceProperty("useServerPrepStmts", "true"); // use server-side prepared statements
        config.addDataSourceProperty("characterEncoding", "utf8"); // Use UTF8
        config.addDataSourceProperty("useUnicode", "true"); // Use UTF8
        config.setConnectionInitSql("SET time_zone = '+00:00'"); // Use UTC timezone

        config.setLeakDetectionThreshold(60 * 1000);
    }

    public boolean isSQLite() {
        return file != null;
    }

    public boolean isMySQL() {
        return !isSQLite();
    }

    public void closeResources(ResultSet rs, PreparedStatement ps) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static @Nullable
    ConnectionWrapper getConnectionWrapper() {
        DatabaseManager databaseManager = Chat2Go.getDatabaseManager();
        ConnectionWrapper connectionWrapper = null;

        try {
            Connection connection = databaseManager.getDataSource().getConnection();
            connectionWrapper = new ConnectionWrapper(connection);
        } catch (SQLException exception) {
            Chat2Go.getInstance().getLogger().log(
                    Level.SEVERE,
                    "Could not connect to database: " + exception.getMessage()
            );
        }

        return connectionWrapper;
    }
}
