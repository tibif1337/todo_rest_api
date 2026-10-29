package com.example.todo.cucumber;

import java.sql.Connection;
import java.sql.Statement;

import org.glassfish.grizzly.http.server.HttpServer;

import com.example.todo.Application;
import com.example.todo.config.DatabaseConfig;

import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;

public class CucumberHooks {

    private static HttpServer server;

    @BeforeAll
    public static void startServer() {
        server = Application.createServer(8081);
    }

    @AfterAll
    public static void stopServer() {
        if (server != null) {
            server.shutdownNow();
        }
    }

    @Before
    public void resetDatabase() {
        try (
                Connection connection =
                        DatabaseConfig.getConnection();

                Statement statement =
                        connection.createStatement()
        ) {
            statement.execute("""
                    TRUNCATE TABLE
                        list_items,
                        todo_items,
                        todo_lists,
                        users
                    RESTART IDENTITY CASCADE
                    """);

        } catch (Exception exception) {
            throw new RuntimeException(
                    "Failed to reset test database.",
                    exception
            );
        }
    }
}