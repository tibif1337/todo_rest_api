package com.example.todo.service;

import java.util.List;
import java.util.Optional;

import com.example.todo.dao.TodoItemDAO;
import com.example.todo.model.TodoItem;

public class TodoItemService {

    private final TodoItemDAO todoItemDAO;

    public TodoItemService(TodoItemDAO todoItemDAO) {
        this.todoItemDAO = todoItemDAO;
    }

    public List<TodoItem> getAll(Long userId) {
        return todoItemDAO.findByUserId(userId);
    }

    public Optional<TodoItem> getById(
            Long id,
            Long userId
    ) {
        return todoItemDAO.findById(id, userId);
    }

    public TodoItem create(
            TodoItem item,
            Long userId
    ) {
        validate(item);

        item.setUserId(userId);

        return todoItemDAO.create(item);
    }

    public Optional<TodoItem> update(
            Long id,
            TodoItem item,
            Long userId
    ) {
        validate(item);

        item.setId(id);
        item.setUserId(userId);

        return todoItemDAO.update(item);
    }

    public boolean delete(
            Long id,
            Long userId
    ) {
        return todoItemDAO.delete(id, userId);
    }

    public Optional<TodoItem> toggle(
            Long id,
            Long userId
    ) {
        if (!todoItemDAO.toggle(id, userId)) {
            return Optional.empty();
        }

        return todoItemDAO.findById(id, userId);
    }

    private void validate(TodoItem item) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "Todo item is required."
            );
        }

        if (item.getName() == null ||
                item.getName().isBlank()) {

            throw new IllegalArgumentException(
                    "Todo item name is required."
            );
        }

        if (item.getDeadline() == null) {
            throw new IllegalArgumentException(
                    "Todo item deadline is required."
            );
        }
    }
}