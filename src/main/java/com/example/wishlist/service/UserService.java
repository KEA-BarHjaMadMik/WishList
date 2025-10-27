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
        // Try to retrieve the username by username or email
        User user = repository.getUser(uid);

        // Check if username exists and password matches
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

    public User getUserByUsername(String username) {
        return repository.getUserByUsername(username);
    }

    public boolean updateUser(String currentUsername, User updatedUser) {
        return repository.updateUser(currentUsername, updatedUser);
    }

    public boolean changePassword(String username, String newPassword) {
        return repository.changePassword(username, newPassword);
    }

    public boolean deleteUser(String username) {
        return repository.deleteUser(username);
    }
}

