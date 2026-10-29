package com.example.todo.dao;

import java.util.List;
import java.util.Optional;

import com.example.todo.model.TodoList;

public interface TodoListDAO {

    List<TodoList> findByUserId(Long userId);

    Optional<TodoList> findById(Long id, Long userId);

    TodoList create(TodoList list);

    Optional<TodoList> update(TodoList list);

    boolean delete(Long id, Long userId);

    boolean setCompleted(Long id, Long userId, boolean completed);

    boolean allItemsCompleted(Long listId, Long userId);
}