package com.example.todo.cucumber;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TodoListSteps {

    private final ApiClient apiClient;
    private final TestContext context;

    public TodoListSteps(
            ApiClient apiClient,
            TestContext context
    ) {
        this.apiClient = apiClient;
        this.context = context;
    }

    @When(
            "I create a todo list named {string}"
    )
    public void createTodoList(String name) {
        ApiClient.ApiResponse response =
                apiClient.post(
                        "/api/lists",
                        new TodoListRequest(
                                name,
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

            context.setListId(
                    response.body()
                            .get("id")
                            .asLong()
            );

            context.setListName(name);
        }
    }

    @Given(
            "I have a todo list named {string}"
    )
    public void createListAsGiven(String name) {
        createTodoList(name);

        assertEquals(
                201,
                context.getLastStatusCode()
        );

        if ("School".equals(name)) {
            context.setListId(
                    context.getLastResponse()
                            .get("id")
                            .asLong()
            );
            context.setListName(name);
        } else {
            context.setSecondListId(
                    context.getLastResponse()
                            .get("id")
                            .asLong()
            );
            context.setSecondListName(name);
        }
    }

    @Given(
            "the list contains a todo item named {string}"
    )
    public void listContainsItem(String itemName) {
        ApiClient.ApiResponse response =
                apiClient.post(
                        "/api/items",
                        new ItemRequest(
                                itemName,
                                "2099-12-31T23:59:00Z",
                                false
                        ),
                        context.getToken()
                );

        assertEquals(
                201,
                response.statusCode()
        );

        Long itemId =
                response.body()
                        .get("id")
                        .asLong();

        Long listId = context.getListId();

        ApiClient.ApiResponse relation =
                apiClient.post(
                        "/api/lists/" +
                                listId +
                                "/items/" +
                                itemId,
                        null,
                        context.getToken()
                );

        assertEquals(
                201,
                relation.statusCode()
        );
    }

    @When(
            "I explicitly complete the list"
    )
    public void explicitlyCompleteList() {
        ApiClient.ApiResponse response =
                apiClient.patch(
                        "/api/lists/" +
                                context.getListId() +
                                "/complete",
                        new TodoListRequest(
                                context.getListName(),
                                true
                        ),
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
            "I complete all items in the list"
    )
    public void completeAllItems() {
        ApiClient.ApiResponse items =
                apiClient.get(
                        "/api/lists/" +
                                context.getListId() +
                                "/items",
                        context.getToken()
                );

        assertEquals(
                200,
                items.statusCode()
        );

        for (JsonNode item : items.body()) {
            if (!item.get("completed").asBoolean()) {
                ApiClient.ApiResponse toggle =
                        apiClient.patch(
                                "/api/items/" +
                                        item.get("id").asLong() +
                                        "/toggle",
                                null,
                                context.getToken()
                        );

                assertEquals(
                        200,
                        toggle.statusCode()
                );
            }
        }
    }

    @Then(
            "the todo list should be created successfully"
    )
    public void listShouldBeCreated() {
        assertEquals(
                201,
                context.getLastStatusCode()
        );
    }

    @Then(
            "the todo list should have the name {string}"
    )
    public void listShouldHaveName(
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
            "the list should be completed"
    )
    public void listShouldBeCompleted() {
        ApiClient.ApiResponse response =
                apiClient.get(
                        "/api/lists/" +
                                context.getListId(),
                        context.getToken()
                );

        assertEquals(
                200,
                response.statusCode()
        );

        assertTrue(
                response.body()
                        .get("completed")
                        .asBoolean()
        );
    }

    @Then(
            "the todo item should appear in the {string} list"
    )
    public void itemShouldAppearInList(
            String listName
    ) {
        Long listId;

        if (listName.equals(
                context.getListName()
        )) {
            listId = context.getListId();
        } else {
            listId = context.getSecondListId();
        }

        ApiClient.ApiResponse response =
                apiClient.get(
                        "/api/lists/" +
                                listId +
                                "/items",
                        context.getToken()
                );

        assertEquals(
                200,
                response.statusCode()
        );

        boolean found = false;

        for (JsonNode item : response.body()) {
            if (item.get("id").asLong()
                    == context.getItemId()) {

                found = true;
                break;
            }
        }

        assertTrue(found);
    }

    @When(
            "I add the todo item to the {string} list"
    )
    public void addItemToList(String listName) {
        Long listId;

        if (listName.equals(
                context.getListName()
        )) {
            listId = context.getListId();
        } else {
            listId = context.getSecondListId();
        }

        ApiClient.ApiResponse response =
                apiClient.post(
                        "/api/lists/" +
                                listId +
                                "/items/" +
                                context.getItemId(),
                        null,
                        context.getToken()
                );

        assertEquals(
                201,
                response.statusCode()
        );
    }

    private record TodoListRequest(
            String name,
            boolean completed
    ) {
    }

    private record ItemRequest(
            String name,
            String deadline,
            boolean completed
    ) {
    }
}