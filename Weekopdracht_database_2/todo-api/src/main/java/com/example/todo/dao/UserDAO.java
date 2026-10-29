package com.example.todo.dao;

import java.util.Optional;

import com.example.todo.model.User;

public interface UserDAO {

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    User create(User user);
}