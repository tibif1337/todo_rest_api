package com.example.todo.service;

import org.mindrot.jbcrypt.BCrypt;

import com.example.todo.dao.UserDAO;
import com.example.todo.model.User;
import com.example.todo.security.JwtService;

public class AuthService {

    private final UserDAO userDAO;
    private final JwtService jwtService;

    public AuthService(
            UserDAO userDAO,
            JwtService jwtService
    ) {
        this.userDAO = userDAO;
        this.jwtService = jwtService;
    }

    public User register(
            String username,
            String password
    ) {
        validateCredentials(username, password);

        if (userDAO.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException(
                    "Username already exists."
            );
        }

        String passwordHash = BCrypt.hashpw(
                password,
                BCrypt.gensalt()
        );

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordHash);

        return userDAO.create(user);
    }

    public String login(
            String username,
            String password
    ) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid username or password."
                        )
                );

        if (!BCrypt.checkpw(
                password,
                user.getPasswordHash()
        )) {
            throw new IllegalArgumentException(
                    "Invalid username or password."
            );
        }

        return jwtService.createToken(user.getId());
    }

    private void validateCredentials(
            String username,
            String password
    ) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username is required."
            );
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required."
            );
        }
    }
}