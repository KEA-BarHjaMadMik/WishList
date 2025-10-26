package com.example.wishlist.repository;

import com.example.wishlist.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User getUser(String uid) {
        // Try finding by username
        User user = getUserByUsername(uid);
        if (user != null) {
            return user;
        }

        // If not found, and uid looks like an email, try finding by email
        if (isValidEmail(uid)) {
            user = getUserByEmail(uid);
        }

        return user;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }


    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM user_account WHERE username = ?";

        RowMapper<User> rowMapper = getUserRowMapper();

        List<User> results = jdbcTemplate.query(sql,rowMapper,username);
        return results.isEmpty() ? null : results.getFirst();
    }

    private User getUserByEmail(String email) {
        String sql = "SELECT * FROM user_account WHERE email = ?";

        RowMapper<User> rowMapper = getUserRowMapper();

        List<User> results = jdbcTemplate.query(sql,rowMapper,email);
        return results.isEmpty() ? null : results.getFirst();
    }

    private RowMapper<User> getUserRowMapper() {
        return (rs, rowNum) -> new User(
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email")
        );
    }


    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM user_account WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM user_account WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public boolean registerUser(User user) {
        String sql = "INSERT INTO user_account (username, password, email) VALUES (?, ?, ?)";
        try {
            // Execute the insert and get the number of affected rows
            int rowsAffected = jdbcTemplate.update(sql,user.getUsername(), user.getPassword(), user.getEmail());
            // Return true only if exactly one row was inserted
            return rowsAffected == 1;
        } catch (DataAccessException e) {
            // Handle any database-related exceptions
            System.err.println("Database error during registration: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUser(String currentUsername, User updatedUser) {
        String sql = "UPDATE user_account SET username = ?, email = ? WHERE username = ?";

        try {
            int rowsAffected = jdbcTemplate.update(
                    sql,
                    updatedUser.getUsername(),
                    updatedUser.getEmail(),
                    currentUsername
            );
            return rowsAffected == 1;
        } catch (DataAccessException e) {
            System.err.println("Database error during user update: " + e.getMessage());
            return false;
        }
    }

    public boolean changePassword(String username, String password) {
        String sql = "UPDATE user_account SET password = ? WHERE username = ?";

        try {
            int rowsAffected = jdbcTemplate.update(sql, password, username);
            return rowsAffected == 1;
        } catch (DataAccessException e) {
            System.err.println("Database error during password change: " + e.getMessage());
            return false;
        }
    }
}
