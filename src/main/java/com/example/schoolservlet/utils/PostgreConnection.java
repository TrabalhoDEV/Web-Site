package com.example.schoolservlet.utils;

import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Utility class for managing PostgreSQL database connections using HikariCP connection pool.
 *
 * <p>This class handles:
 * <ul>
 *   <li>Loading environment variables for database configuration</li>
 *   <li>Initializing HikariCP connection pool with custom settings</li>
 *   <li>Providing methods to obtain and close connections</li>
 *   <li>Graceful shutdown of the connection pool</li>
 * </ul>
 */
public class PostgreConnection {

    /**
     * Static initializer block that configures and initializes the HikariCP connection pool.
     *
     * <p>Responsibilities include:
     * <ul>
     *   <li>Loading environment variables from a .env file if available</li>
     *   <li>Reading database URL, user, and password from environment or config service</li>
     *   <li>Loading the PostgreSQL JDBC driver</li>
     *   <li>Setting HikariCP configuration such as pool size, timeouts, auto-commit, and prepared statement caching</li>
     *   <li>Initializing the static dataSource instance with the configured HikariCP settings</li>
     * </ul>
     */
    private  static HikariDataSource dataSource;

    public static void start(){
        Dotenv dotenv = null;

        // Firstly, it tries to load .env locally
        try {
            dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
        } catch (Exception e) {
            e.printStackTrace();
            dotenv = null;
        }

        String dbUrl = ConfigService.getEnv("DB_URL", dotenv);
        String dbUser = ConfigService.getEnv("DB_USER", dotenv);
        String dbPassword = ConfigService.getEnv("DB_PASSWORD", dotenv);

        try{
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e){
            throw new RuntimeException("PostgreSQL Driver not found ", e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);

//        Connection pool settings:
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30 * 10 * 10 * 10);
        config.setIdleTimeout(6 * 10 * 10 * 10 *10);
        config.setMaxLifetime(18 * 10 * 10 * 10 * 10);
        config.setAutoCommit(true);
        config.setConnectionTestQuery("SELECT 1");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection()  throws SQLException{
        return dataSource.getConnection();
    }

    /**
     * Closes the provided database connection if it is not null and not already closed.
     *
     * <p>Any SQLException encountered during closing is caught, printed, and rethrown as a RuntimeException.
     *
     * @param conn the Connection object to be closed
     * @throws RuntimeException if an error occurs while closing the connection
     */
    public static void disconnect(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao fechar conexão");
        }
    }

    /**
     * Shuts down the HikariCP connection pool by closing the dataSource if it is open.
     *
     * <p>This method ensures that all pooled connections are properly released and the pool is terminated.
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
