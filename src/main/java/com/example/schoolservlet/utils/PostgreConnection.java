package com.example.schoolservlet.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class PostgreConnection {
<<<<<<< HEAD

    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASSWORD;

    static{
=======
    public static Connection getConnection() {
>>>>>>> 6e33e589d430f2f48d0a3e464132537d3bfe8ce9
        Dotenv dotenv = null;

        // Firstly, it tries to load .env locally
        try {
            dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
        } catch (Exception e) {
<<<<<<< HEAD
            e.printStackTrace();
            dotenv = null;
        }

        DB_URL = ConfigService.getEnv("DB_URL", dotenv);
        DB_USER = ConfigService.getEnv("DB_USER", dotenv);
        DB_PASSWORD = ConfigService.getEnv("DB_PASSWORD", dotenv);
    }

    public static Connection getConnection() {
        if (DB_URL == null || DB_USER == null || DB_PASSWORD == null) {
            throw new RuntimeException("Variáveis de ambiente não configuradas");
        }
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
=======
            dotenv = null;
        }


        // Create variables and tries to get their values loccaly,
        // if not it gets from system beacause it's in production maybe in Render

        String url = System.getenv("DB_URL");
        if (url == null && dotenv != null) url = dotenv.get("DB_URL");

        String usuario = System.getenv("DB_USER");
        if (usuario == null && dotenv != null) usuario = dotenv.get("DB_USER");

        String senha = System.getenv("DB_PASSWORD");
        if (senha == null && dotenv != null) senha = dotenv.get("DB_PASSWORD");

        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, usuario, senha);
            return conn;
>>>>>>> 6e33e589d430f2f48d0a3e464132537d3bfe8ce9
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
