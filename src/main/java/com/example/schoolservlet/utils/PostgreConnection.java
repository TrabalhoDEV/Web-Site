package com.example.schoolservlet.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class PostgreConnection {

    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASSWORD;

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

        DB_URL = ConfigService.getEnv("DB_URL", dotenv);
        DB_USER = ConfigService.getEnv("DB_USER", dotenv);
        DB_PASSWORD = ConfigService.getEnv("DB_PASSWORD", dotenv);

        if (DB_URL == null || DB_USER == null || DB_PASSWORD == null){
            throw new RuntimeException("Variáveis de ambiente não foram setadas");
        }
    }

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
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
}
