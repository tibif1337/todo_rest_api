package com.example.todo.dao;

import java.util.List;

import com.example.todo.model.TodoItem;

public interface ListItemDAO {

    boolean addItemToList(
            Long listId,
            Long itemId,
            Long userId
    );

    boolean removeItemFromList(
            Long listId,
            Long itemId,
            Long userId
    );

    List<TodoItem> findItemsByListId(
            Long listId,
            Long userId
    );

    boolean exists(
            Long listId,
            Long itemId,
            Long userId
    );
}