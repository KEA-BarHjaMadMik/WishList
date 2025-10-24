package com.example.wishlist.service;

import com.example.wishlist.model.User;
import com.example.wishlist.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User authenticate(String uid, String pw) {
        // Try to retrieve the user by username or email
        User user = repository.getUser(uid);

        // Check if user exists and password matches
        if (user != null && user.getPassword().equals(pw)) {
            // Authentication successful — return the full User object
            return user;
        }

        // Authentication failed — return null
        return null;
    }

    public boolean usernameExists(String username) {
        return repository.usernameExists(username);
    }

    public boolean emailExists(String email) {
        return repository.emailExists(email);
    }

    public boolean registerUser(User user) {
        return repository.registerUser(user);
    }
}

