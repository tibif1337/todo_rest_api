package com.example.todo.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConfig {

    private static final String DEFAULT_URL =
            "jdbc:postgresql://localhost:5432/todo_app";

    private DatabaseConfig() {
    }

    public static Connection getConnection() throws SQLException {
        String url = System.getenv()
                .getOrDefault("DB_URL", DEFAULT_URL);

        String username = System.getenv()
                .getOrDefault("DB_USERNAME", "postgres");

        String password = System.getenv("DB_PASSWORD");

        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "DB_PASSWORD environment variable is not set."
            );
        }

        return DriverManager.getConnection(
                url,
                username,
                password
        );
    }
}