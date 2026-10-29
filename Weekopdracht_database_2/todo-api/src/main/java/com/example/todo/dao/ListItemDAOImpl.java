package com.example.todo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.todo.config.DatabaseConfig;
import com.example.todo.model.TodoItem;

public class ListItemDAOImpl implements ListItemDAO {

    @Override
    public boolean addItemToList(
            Long listId,
            Long itemId,
            Long userId
    ) {
        String listSql = """
                SELECT id
                FROM todo_lists
                WHERE id = ?
                  AND user_id = ?
                """;

        String itemSql = """
                SELECT id
                FROM todo_items
                WHERE id = ?
                  AND user_id = ?
                """;

        String insertSql = """
                INSERT INTO list_items (list_id, item_id)
                VALUES (?, ?)
                ON CONFLICT DO NOTHING
                """;

        try (Connection connection = DatabaseConfig.getConnection()) {

            if (!ownedEntityExists(
                    connection,
                    listSql,
                    listId,
                    userId
            )) {
                return false;
            }

            if (!ownedEntityExists(
                    connection,
                    itemSql,
                    itemId,
                    userId
            )) {
                return false;
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(insertSql)) {

                statement.setLong(1, listId);
                statement.setLong(2, itemId);

                statement.executeUpdate();
                return true;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to add item to list.",
                    exception
            );
        }
    }

    @Override
    public boolean removeItemFromList(
            Long listId,
            Long itemId,
            Long userId
    ) {
        String sql = """
                DELETE FROM list_items li
                USING todo_lists l, todo_items i
                WHERE li.list_id = l.id
                  AND li.item_id = i.id
                  AND li.list_id = ?
                  AND li.item_id = ?
                  AND l.user_id = ?
                  AND i.user_id = ?
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, listId);
            statement.setLong(2, itemId);
            statement.setLong(3, userId);
            statement.setLong(4, userId);

            return statement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to remove item from list.",
                    exception
            );
        }
    }

    @Override
    public List<TodoItem> findItemsByListId(
            Long listId,
            Long userId
    ) {
        String sql = """
                SELECT
                    i.id,
                    i.user_id,
                    i.name,
                    i.deadline,
                    i.completed,
                    i.created_at,
                    i.updated_at
                FROM todo_items i
                JOIN list_items li
                    ON li.item_id = i.id
                JOIN todo_lists l
                    ON l.id = li.list_id
                WHERE li.list_id = ?
                  AND l.user_id = ?
                  AND i.user_id = ?
                ORDER BY i.id
                """;

        List<TodoItem> items = new ArrayList<>();

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, listId);
            statement.setLong(2, userId);
            statement.setLong(3, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapItem(resultSet));
                }
            }

            return items;
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to find list items.",
                    exception
            );
        }
    }

    @Override
    public boolean exists(
            Long listId,
            Long itemId,
            Long userId
    ) {
        String sql = """
                SELECT 1
                FROM list_items li
                JOIN todo_lists l
                    ON l.id = li.list_id
                JOIN todo_items i
                    ON i.id = li.item_id
                WHERE li.list_id = ?
                  AND li.item_id = ?
                  AND l.user_id = ?
                  AND i.user_id = ?
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, listId);
            statement.setLong(2, itemId);
            statement.setLong(3, userId);
            statement.setLong(4, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Failed to check list-item relationship.",
                    exception
            );
        }
    }

    private boolean ownedEntityExists(
            Connection connection,
            String sql,
            Long id,
            Long userId
    ) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.setLong(2, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private TodoItem mapItem(ResultSet resultSet) throws SQLException {
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