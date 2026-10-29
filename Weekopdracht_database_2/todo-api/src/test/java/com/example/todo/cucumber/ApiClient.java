package com.example.todo.cucumber;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ApiClient {

    private final HttpClient client;
    private final ObjectMapper objectMapper;

    private final String baseUrl;

    public ApiClient() {
        // this.client = HttpClient.newHttpClient();
        this.client = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .build();

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(
                new JavaTimeModule()
        );

        this.baseUrl = "http://127.0.0.1:8081";
    }

    public ApiResponse get(
            String path,
            String token
    ) {
        return send(
                "GET",
                path,
                null,
                token
        );
    }

    public ApiResponse post(
            String path,
            Object body,
            String token
    ) {
        return send(
                "POST",
                path,
                body,
                token
        );
    }

    public ApiResponse put(
            String path,
            Object body,
            String token
    ) {
        return send(
                "PUT",
                path,
                body,
                token
        );
    }

    public ApiResponse patch(
            String path,
            Object body,
            String token
    ) {
        return send(
                "PATCH",
                path,
                body,
                token
        );
    }

    public ApiResponse delete(
            String path,
            String token
    ) {
        return send(
                "DELETE",
                path,
                null,
                token
        );
    }

    private ApiResponse send(
            String method,
            String path,
            Object body,
            String token
    ) {
        try {
            HttpRequest.Builder builder =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            baseUrl + path
                                    )
                            )
                            .header(
                                    "Accept",
                                    "application/json"
                            );

            if (token != null) {
                builder.header(
                        "Authorization",
                        "Bearer " + token
                );
            }

            if (body != null) {
                String json =
                        objectMapper.writeValueAsString(body);

                builder.header(
                        "Content-Type",
                        "application/json"
                );

                builder.method(
                        method,
                        HttpRequest.BodyPublishers.ofString(
                                json
                        )
                );
            } else {
                builder.method(
                        method,
                        HttpRequest.BodyPublishers.noBody()
                );
            }

            HttpResponse<String> response =
                    client.send(
                            builder.build(),
                            HttpResponse.BodyHandlers.ofString()
                    );

            JsonNode jsonNode = null;

            if (!response.body().isBlank()) {
                jsonNode =
                        objectMapper.readTree(
                                response.body()
                        );
            }

            return new ApiResponse(
                    response.statusCode(),
                    jsonNode
            );

        } catch (IOException | InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "HTTP request failed.",
                    exception
            );
        }
    }

    public record ApiResponse(
            int statusCode,
            JsonNode body
    ) {
    }
}