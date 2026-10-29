package com.example.todo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.todo.config.DatabaseConfig;
import com.example.todo.model.TodoList;

public class TodoListDAOImpl implements TodoListDAO {

    private static final String SELECT_COLUMNS = """
            id,
            user_id,
            name,
            completed,
            created_at,
            updated_at
            """;

    @Override
    public List<TodoList> findByUserId(Long userId) {
        String sql = """
                SELECT %s
                FROM todo_lists
                WHERE user_id = ?
                ORDER BY id
                """.formatted(SELECT_COLUMNS);

        List<TodoList> lists = new ArrayList<>();

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    lists.add(mapRow(resultSet));
                }
            }

            return lists;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find todo lists.",
                    exception
            );
        }
    }

    @Override
    public Optional<TodoList> findById(Long id, Long userId) {
        String sql = """
                SELECT %s
                FROM todo_lists
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
                    "Failed to find todo list.",
                    exception
            );
        }
    }

    @Override
    public TodoList create(TodoList list) {
        String sql = """
                INSERT INTO todo_lists
                    (user_id, name, completed)
                VALUES
                    (?, ?, ?)
                RETURNING %s
                """.formatted(SELECT_COLUMNS);

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, list.getUserId());
            statement.setString(2, list.getName());
            statement.setBoolean(3, list.isCompleted());

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return mapRow(resultSet);
            }
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to create todo list.",
                    exception
            );
        }
    }

    @Override
    public Optional<TodoList> update(TodoList list) {
        String sql = """
                UPDATE todo_lists
                SET name = ?,
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
            statement.setString(1, list.getName());
            statement.setBoolean(2, list.isCompleted());
            statement.setLong(3, list.getId());
            statement.setLong(4, list.getUserId());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }

                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to update todo list.",
                    exception
            );
        }
    }

    @Override
    public boolean delete(Long id, Long userId) {
        String sql = """
                DELETE FROM todo_lists
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
                    "Failed to delete todo list.",
                    exception
            );
        }
    }

    @Override
    public boolean setCompleted(
            Long id,
            Long userId,
            boolean completed
    ) {
        String sql = """
                UPDATE todo_lists
                SET completed = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                  AND user_id = ?
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setBoolean(1, completed);
            statement.setLong(2, id);
            statement.setLong(3, userId);

            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to update list completion.",
                    exception
            );
        }
    }

    @Override
    public boolean allItemsCompleted(
            Long listId,
            Long userId
    ) {
        String sql = """
                SELECT
                    COUNT(*) > 0
                    AND COUNT(*) FILTER (
                        WHERE i.completed = FALSE
                    ) = 0 AS all_completed
                FROM list_items li
                JOIN todo_items i
                    ON i.id = li.item_id
                JOIN todo_lists l
                    ON l.id = li.list_id
                WHERE li.list_id = ?
                  AND l.user_id = ?
                  AND i.user_id = ?
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, listId);
            statement.setLong(2, userId);
            statement.setLong(3, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getBoolean("all_completed");
            }
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to check list completion.",
                    exception
            );
        }
    }

    private TodoList mapRow(ResultSet resultSet) throws SQLException {
        return new TodoList(
                resultSet.getLong("id"),
                resultSet.getLong("user_id"),
                resultSet.getString("name"),
                resultSet.getBoolean("completed"),
                resultSet.getTimestamp("created_at").toInstant(),
                resultSet.getTimestamp("updated_at").toInstant()
        );
    }
}