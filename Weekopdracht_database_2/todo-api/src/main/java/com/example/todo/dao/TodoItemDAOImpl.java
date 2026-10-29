package com.example.todo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.todo.config.DatabaseConfig;
import com.example.todo.model.TodoItem;

public class TodoItemDAOImpl implements TodoItemDAO {

    private static final String SELECT_COLUMNS = """
            id,
            user_id,
            name,
            deadline,
            completed,
            created_at,
            updated_at
            """;

    @Override
    public List<TodoItem> findByUserId(Long userId) {
        String sql = """
                SELECT %s
                FROM todo_items
                WHERE user_id = ?
                ORDER BY id
                """.formatted(SELECT_COLUMNS);

        List<TodoItem> items = new ArrayList<>();

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapRow(resultSet));
                }
            }

            return items;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find todo items.",
                    exception
            );
        }
    }

    @Override
    public Optional<TodoItem> findById(Long id, Long userId) {
        String sql = """
                SELECT %s
                FROM todo_items
                WHERE id = ?
                  AND user_id = ?
                """.formatted(SELECT_COLUMNS);

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            statement.setLong(2, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }

                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find todo item.",
                    exception
            );
        }
    }

    @Override
    public TodoItem create(TodoItem item) {
        String sql = """
                INSERT INTO todo_items
                    (user_id, name, deadline, completed)
                VALUES
                    (?, ?, ?, ?)
                RETURNING %s
                """.formatted(SELECT_COLUMNS);

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, item.getUserId());
            statement.setString(2, item.getName());
            statement.setTimestamp(
                    3,
                    Timestamp.from(item.getDeadline())
            );
            statement.setBoolean(4, item.isCompleted());

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return mapRow(resultSet);
            }
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to create todo item.",
                    exception
            );
        }
    }

    @Override
    public Optional<TodoItem> update(TodoItem item) {
        String sql = """
                UPDATE todo_items
                SET name = ?,
                    deadline = ?,
                    completed = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                  AND user_id = ?
                RETURNING %s
                """.formatted(SELECT_COLUMNS);

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, item.getName());
            statement.setTimestamp(
                    2,
                    Timestamp.from(item.getDeadline())
            );
            statement.setBoolean(3, item.isCompleted());
            statement.setLong(4, item.getId());
            statement.setLong(5, item.getUserId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }

                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to update todo item.",
                    exception
            );
        }
    }

    @Override
    public boolean delete(Long id, Long userId) {
        String sql = """
                DELETE FROM todo_items
                WHERE id = ?
                  AND user_id = ?
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            statement.setLong(2, userId);

            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to delete todo item.",
                    exception
            );
        }
    }

    @Override
    public boolean toggle(Long id, Long userId) {
        String sql = """
                UPDATE todo_items
                SET completed = NOT completed,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                  AND user_id = ?
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);
            statement.setLong(2, userId);

            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to toggle todo item.",
                    exception
            );
        }
    }

    private TodoItem mapRow(ResultSet resultSet) throws SQLException {
        return new TodoItem(
                resultSet.getLong("id"),
                resultSet.getLong("user_id"),
                resultSet.getString("name"),
                resultSet.getTimestamp("deadline").toInstant(),
                resultSet.getBoolean("completed"),
                resultSet.getTimestamp("created_at").toInstant(),
                resultSet.getTimestamp("updated_at").toInstant()
        );
    }
}