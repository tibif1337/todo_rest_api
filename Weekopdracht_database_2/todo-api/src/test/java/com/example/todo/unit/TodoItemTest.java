package com.example.todo.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.example.todo.model.TodoItem;

class TodoItemTest {

    @Test
    void itemWithPastDeadlineIsLate() {
        TodoItem item = new TodoItem();

        item.setName("Late task");
        item.setDeadline(
                Instant.now().minusSeconds(60)
        );

        assertTrue(item.isPastDeadline());
    }

    @Test
    void itemWithFutureDeadlineIsNotLate() {
        TodoItem item = new TodoItem();

        item.setName("Future task");
        item.setDeadline(
                Instant.now().plusSeconds(3600)
        );

        assertFalse(item.isPastDeadline());
    }

    @Test
    void completedItemCanStillBeLate() {
        TodoItem item = new TodoItem();

        item.setName("Late completed task");
        item.setDeadline(
                Instant.now().minusSeconds(60)
        );
        item.setCompleted(true);

        assertTrue(item.isCompleted());
        assertTrue(item.isPastDeadline());
    }

    @Test
    void itemCanBeToggledCompleted() {
        TodoItem item = new TodoItem();

        assertFalse(item.isCompleted());

        item.setCompleted(true);

        assertTrue(item.isCompleted());

        item.setCompleted(false);

        assertFalse(item.isCompleted());
    }
}