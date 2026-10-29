package com.example.todo.resource;

import com.example.todo.dao.UserDAO;
import com.example.todo.dao.UserDAOImpl;
import com.example.todo.model.AuthRequest;
import com.example.todo.model.LoginResponse;
import com.example.todo.model.User;
import com.example.todo.security.JwtService;
import com.example.todo.service.AuthService;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    private final AuthService authService;

    public AuthResource() {
        UserDAO userDAO = new UserDAOImpl();
        JwtService jwtService = new JwtService();

        this.authService = new AuthService(
                userDAO,
                jwtService
        );
    }

    @POST
    @Path("/register")
    public Response register(AuthRequest request) {
        try {
            User user = authService.register(
                    request.getUsername(),
                    request.getPassword()
            );

            return Response.status(
                    Response.Status.CREATED
            ).entity(user).build();

        } catch (IllegalArgumentException exception) {
            return Response.status(
                    Response.Status.CONFLICT
            ).entity(
                    java.util.Map.of(
                            "error",
                            exception.getMessage()
                    )
            ).build();
        }
    }

    @POST
    @Path("/login")
    public Response login(AuthRequest request) {
        try {
            String token = authService.login(
                    request.getUsername(),
                    request.getPassword()
            );

            return Response.ok(
                    new LoginResponse(token)
            ).build();

        } catch (IllegalArgumentException exception) {
            return Response.status(
                    Response.Status.UNAUTHORIZED
            ).entity(
                    java.util.Map.of(
                            "error",
                            exception.getMessage()
                    )
            ).build();
        }
    }
}