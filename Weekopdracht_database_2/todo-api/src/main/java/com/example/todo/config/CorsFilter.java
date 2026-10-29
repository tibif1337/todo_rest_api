package com.example.todo.config;

import java.io.IOException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION - 1)
public class CorsFilter implements
        ContainerRequestFilter,
        ContainerResponseFilter {

    @Override
    public void filter(
            ContainerRequestContext requestContext
    ) throws IOException {

        // Browsers send OPTIONS requests before certain
        // cross-origin requests. They must not require a JWT.
        if ("OPTIONS".equalsIgnoreCase(
                requestContext.getMethod())) {

            requestContext.abortWith(
                    Response.noContent().build()
            );
        }
    }

    @Override
    public void filter(
            ContainerRequestContext requestContext,
            ContainerResponseContext responseContext
    ) throws IOException {

        String origin = requestContext
                .getHeaderString("Origin");

        if (origin == null || !isAllowedOrigin(origin)) {
            return;
        }

        responseContext.getHeaders().putSingle(
                "Access-Control-Allow-Origin",
                origin
        );

        responseContext.getHeaders().putSingle(
                "Vary",
                "Origin"
        );

        responseContext.getHeaders().putSingle(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, PATCH, DELETE, OPTIONS"
        );

        responseContext.getHeaders().putSingle(
                "Access-Control-Allow-Headers",
                "Content-Type, Authorization, Accept"
        );
    }

    private boolean isAllowedOrigin(String origin) {
        return origin.matches(
                "^http://(localhost|127\\.0\\.0\\.1):[0-9]+$"
        );
    }
}
