package com.example.todo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.example.todo.config.DatabaseConfig;
import com.example.todo.model.User;

public class UserDAOImpl implements UserDAO {

    @Override
    public Optional<User> findById(Long id) {
        String sql = """
                SELECT id, username, password_hash, created_at
                FROM users
                WHERE id = ?
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }

                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find user by ID.",
                    exception
            );
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = """
                SELECT id, username, password_hash, created_at
                FROM users
                WHERE username = ?
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }

                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find user by username.",
                    exception
            );
        }
    }

    @Override
    public User create(User user) {
        String sql = """
                INSERT INTO users (username, password_hash)
                VALUES (?, ?)
                RETURNING id, username, password_hash, created_at
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return mapRow(resultSet);
            }
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to create user.",
                    exception
            );
        }
    }

    private User mapRow(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("username"),
                resultSet.getString("password_hash"),
                resultSet.getTimestamp("created_at").toInstant()
        );
    }
}