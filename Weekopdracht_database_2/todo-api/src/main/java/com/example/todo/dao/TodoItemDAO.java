package com.example.todo.dao;

import java.util.List;
import java.util.Optional;

import com.example.todo.model.TodoItem;

public interface TodoItemDAO {

    List<TodoItem> findByUserId(Long userId);

    Optional<TodoItem> findById(Long id, Long userId);

    TodoItem create(TodoItem item);

    Optional<TodoItem> update(TodoItem item);

    boolean delete(Long id, Long userId);

    boolean toggle(Long id, Long userId);
}