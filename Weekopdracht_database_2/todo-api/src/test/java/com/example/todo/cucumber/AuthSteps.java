package com.example.todo.cucumber;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AuthSteps {

    private final ApiClient apiClient;
    private final TestContext context;

    public AuthSteps(
            ApiClient apiClient,
            TestContext context
    ) {
        this.apiClient = apiClient;
        this.context = context;
    }

    @Given(
            "I am logged in as {string} with password {string}"
    )
    public void iAmLoggedIn(
            String username,
            String password
    ) {
        ApiClient.ApiResponse registerResponse =
                apiClient.post(
                        "/api/auth/register",
                        new Credentials(
                                username,
                                password
                        ),
                        null
                );

        assertEquals(
                201,
                registerResponse.statusCode()
        );

        ApiClient.ApiResponse loginResponse =
                apiClient.post(
                        "/api/auth/login",
                        new Credentials(
                                username,
                                password
                        ),
                        null
                );

        assertEquals(
                200,
                loginResponse.statusCode()
        );

        JsonNode body =
                loginResponse.body();

        assertNotNull(body);
        assertTrue(body.has("token"));

        context.setToken(
                body.get("token").asText()
        );
    }

    @When(
            "I try to log in as {string} with password {string}"
    )
    public void iTryToLogin(
            String username,
            String password
    ) {
        ApiClient.ApiResponse response =
                apiClient.post(
                        "/api/auth/login",
                        new Credentials(
                                username,
                                password
                        ),
                        null
                );

        context.setLastStatusCode(
                response.statusCode()
        );

        context.setLastResponse(
                response.body()
        );
    }

    @Then(
            "the login should be successful"
    )
    public void loginShouldBeSuccessful() {
        assertEquals(
                200,
                context.getLastStatusCode()
        );
    }

    @Then(
            "the login should fail"
    )
    public void loginShouldFail() {
        assertEquals(
                401,
                context.getLastStatusCode()
        );
    }

    private record Credentials(
            String username,
            String password
    ) {
    }
}