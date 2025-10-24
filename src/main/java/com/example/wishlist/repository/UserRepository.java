package com.example.wishlist.repository;

import com.example.wishlist.model.User;
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


    private User getUserByUsername(String username) {
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
        return (rs, rowNum) -> {
            return new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("email")
            );
        };
    }
}
