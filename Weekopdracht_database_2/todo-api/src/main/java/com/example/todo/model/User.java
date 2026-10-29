package com.example.todo.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class User {

    private Long id;
    private String username;

    @JsonIgnore
    private String passwordHash;

    private Instant createdAt;

    public User() {
    }

    public User(
            Long id,
            String username,
            String passwordHash,
            Instant createdAt
    ) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}