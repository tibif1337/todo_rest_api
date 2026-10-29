package com.example.todo.cucumber;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TodoItemSteps {

    private final ApiClient apiClient;
    private final TestContext context;

    public TodoItemSteps(
            ApiClient apiClient,
            TestContext context
    ) {
        this.apiClient = apiClient;
        this.context = context;
    }

    @When(
            "I create a todo item named {string} with deadline {string}"
    )
    public void createTodoItem(
            String name,
            String deadline
    ) {
        ApiClient.ApiResponse response =
                apiClient.post(
                        "/api/items",
                        new TodoItemRequest(
                                name,
                                deadline,
                                false
                        ),
                        context.getToken()
                );

        context.setLastStatusCode(
                response.statusCode()
        );

        context.setLastResponse(
                response.body()
        );

        if (response.body() != null &&
                response.body().has("id")) {

            context.setItemId(
                    response.body()
                            .get("id")
                            .asLong()
            );

            context.setItemName(name);
        }
    }

    @Given(
            "I have a todo item named {string} with deadline {string}"
    )
    public void createItemAsGiven(
            String name,
            String deadline
    ) {
        createTodoItem(name, deadline);

        assertEquals(
                201,
                context.getLastStatusCode()
        );
    }

    @Given(
            "I have a todo item named {string}"
    )
    public void createItemWithoutExplicitDeadline(
            String name
    ) {
        createTodoItem(
                name,
                "2099-12-31T23:59:00Z"
        );
    }

    @Given(
            "the todo item is not completed"
    )
    public void itemIsNotCompleted() {
        assertNotNull(context.getItemId());

        ApiClient.ApiResponse response =
                apiClient.get(
                        "/api/items/" +
                                context.getItemId(),
                        context.getToken()
                );

        assertEquals(
                200,
                response.statusCode()
        );

        assertFalse(
                response.body()
                        .get("completed")
                        .asBoolean()
        );
    }

    @When(
            "I toggle the todo item"
    )
    public void toggleTodoItem() {
        ApiClient.ApiResponse response =
                apiClient.patch(
                        "/api/items/" +
                                context.getItemId() +
                                "/toggle",
                        null,
                        context.getToken()
                );

        context.setLastStatusCode(
                response.statusCode()
        );

        context.setLastResponse(
                response.body()
        );
    }

    @When(
            "I toggle the todo item again"
    )
    public void toggleTodoItemAgain() {
        toggleTodoItem();
    }

    @Then(
            "the todo item should be created successfully"
    )
    public void itemShouldBeCreated() {
        assertEquals(
                201,
                context.getLastStatusCode()
        );
    }

    @Then(
            "the todo item should have the name {string}"
    )
    public void itemShouldHaveName(
            String expectedName
    ) {
        assertEquals(
                expectedName,
                context.getLastResponse()
                        .get("name")
                        .asText()
        );
    }

    @Then(
            "the todo item should be completed"
    )
    public void itemShouldBeCompleted() {
        assertTrue(
                context.getLastResponse()
                        .get("completed")
                        .asBoolean()
        );
    }

    @Then(
            "the todo item should not be completed"
    )
    public void itemShouldNotBeCompleted() {
        assertFalse(
                context.getLastResponse()
                        .get("completed")
                        .asBoolean()
        );
    }

    @Then(
            "the todo item should be marked as late"
    )
    public void itemShouldBeLate() {
        ApiClient.ApiResponse response =
                apiClient.get(
                        "/api/items/" +
                                context.getItemId(),
                        context.getToken()
                );

        assertEquals(
                200,
                response.statusCode()
        );

        JsonNode item = response.body();

        assertTrue(
                item.get("pastDeadline")
                        .asBoolean()
        );
    }

    @Given(
            "I have a todo item named {string} with deadline {string} and it is completed"
    )
    public void createCompletedItem(
            String name,
            String deadline
    ) {
        createTodoItem(name, deadline);

        toggleTodoItem();

        assertEquals(
                200,
                context.getLastStatusCode()
        );
    }

    private record TodoItemRequest(
            String name,
            String deadline,
            boolean completed
    ) {
    }
}