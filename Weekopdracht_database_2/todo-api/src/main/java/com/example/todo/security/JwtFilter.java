package com.example.todo.security;

import java.io.IOException;
import java.security.Principal;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtFilter implements ContainerRequestFilter {

    private final JwtService jwtService = new JwtService();

    @Override
    public void filter(
            ContainerRequestContext requestContext
    ) throws IOException {

        String path = requestContext
                .getUriInfo()
                .getPath();

        if (path.startsWith("api/auth/")) {
            return;
        }

        String authorization =
                requestContext.getHeaderString(
                        "Authorization"
                );

        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {

            abort(requestContext);
            return;
        }

        String token = authorization.substring(7);

        if (!jwtService.isValid(token)) {
            abort(requestContext);
            return;
        }

        Long userId = jwtService.getUserId(token);

        SecurityContext originalContext =
                requestContext.getSecurityContext();

        SecurityContext securityContext =
                new AuthenticatedSecurityContext(
                        userId,
                        originalContext
                );

        requestContext.setSecurityContext(
                securityContext
        );
    }

    private void abort(
            ContainerRequestContext requestContext
    ) {
        requestContext.abortWith(
                Response.status(
                        Response.Status.UNAUTHORIZED
                ).build()
        );
    }

    private static class AuthenticatedSecurityContext
            implements SecurityContext {

        private final Long userId;
        private final SecurityContext originalContext;

        private AuthenticatedSecurityContext(
                Long userId,
                SecurityContext originalContext
        ) {
            this.userId = userId;
            this.originalContext = originalContext;
        }

        @Override
        public Principal getUserPrincipal() {
            return () -> userId.toString();
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public boolean isSecure() {
            return originalContext != null
                    && originalContext.isSecure();
        }

        @Override
        public String getAuthenticationScheme() {
            return "Bearer";
        }
    }
}