package com.example.todo.service;

import java.util.List;
import java.util.Optional;

import com.example.todo.dao.ListItemDAO;
import com.example.todo.dao.TodoListDAO;
import com.example.todo.model.TodoItem;
import com.example.todo.model.TodoList;

public class TodoListService {

    private final TodoListDAO todoListDAO;
    private final ListItemDAO listItemDAO;

    public TodoListService(
            TodoListDAO todoListDAO,
            ListItemDAO listItemDAO
    ) {
        this.todoListDAO = todoListDAO;
        this.listItemDAO = listItemDAO;
    }

    public List<TodoList> getAll(Long userId) {
        return todoListDAO.findByUserId(userId)
                .stream()
                .map(list -> applyEffectiveCompletion(
                        list,
                        userId
                ))
                .toList();
    }

    public Optional<TodoList> getById(
            Long id,
            Long userId
    ) {
        return todoListDAO.findById(id, userId)
                .map(list ->
                        applyEffectiveCompletion(
                                list,
                                userId
                        )
                );
    }

    public TodoList create(
            TodoList list,
            Long userId
    ) {
        validate(list);

        list.setUserId(userId);

        return todoListDAO.create(list);
    }

    public Optional<TodoList> update(
            Long id,
            TodoList list,
            Long userId
    ) {
        validate(list);

        list.setId(id);
        list.setUserId(userId);

        Optional<TodoList> updated =
                todoListDAO.update(list);

        return updated.map(result ->
                applyEffectiveCompletion(
                        result,
                        userId
                )
        );
    }

    public boolean delete(
            Long id,
            Long userId
    ) {
        return todoListDAO.delete(id, userId);
    }

    public Optional<TodoList> setCompleted(
            Long id,
            Long userId,
            boolean completed
    ) {
        if (!todoListDAO.setCompleted(
                id,
                userId,
                completed
        )) {
            return Optional.empty();
        }

        return getById(id, userId);
    }

    public List<TodoItem> getItems(
            Long listId,
            Long userId
    ) {
        if (todoListDAO.findById(
                listId,
                userId
        ).isEmpty()) {
            return List.of();
        }

        return listItemDAO.findItemsByListId(
                listId,
                userId
        );
    }

    public boolean addItem(
            Long listId,
            Long itemId,
            Long userId
    ) {
        return listItemDAO.addItemToList(
                listId,
                itemId,
                userId
        );
    }

    public boolean removeItem(
            Long listId,
            Long itemId,
            Long userId
    ) {
        return listItemDAO.removeItemFromList(
                listId,
                itemId,
                userId
        );
    }

    private TodoList applyEffectiveCompletion(
            TodoList list,
            Long userId
    ) {
        boolean effectiveCompleted =
                list.isCompleted()
                        || todoListDAO.allItemsCompleted(
                                list.getId(),
                                userId
                        );

        list.setCompleted(effectiveCompleted);

        return list;
    }

    private void validate(TodoList list) {
        if (list == null) {
            throw new IllegalArgumentException(
                    "Todo list is required."
            );
        }

        if (list.getName() == null ||
                list.getName().isBlank()) {

            throw new IllegalArgumentException(
                    "Todo list name is required."
            );
        }
    }
}