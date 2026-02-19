package com.example.schoolservlet.utils;

import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

public class PostgreConnection {

    private  static final HikariDataSource dataSource;

    static{
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

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
