package com.example.wishlist.repository;

import com.example.wishlist.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:h2init.sql", executionPhase = BEFORE_TEST_METHOD)
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Test
    void shouldGetUserByUsername() {
        User user = repository.getUserByUsername("test_wisher");

        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("test_wisher");
        assertThat(user.getEmail()).isEqualTo("wisher@test.dk");
        assertThat(user.getPassword()).isEqualTo("test123");
    }

    @Test
    void getUserByEmail() {
        User user = repository.getUserByEmail("wisher@test.dk");

        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("test_wisher");
        assertThat(user.getEmail()).isEqualTo("wisher@test.dk");
        assertThat(user.getPassword()).isEqualTo("test123");
    }

    @Test
    void usernameExistsShouldBeTrue() {
        boolean usernameExists = repository.usernameExists("test_wisher");

        assertThat(usernameExists).isTrue();
    }

    @Test
    void usernameExistsShouldBeFalse() {
        boolean usernameExists = repository.usernameExists("nonExistent");

        assertThat(usernameExists).isFalse();
    }

    @Test
    void emailExistsShouldBeTrue() {
        boolean emailExists = repository.emailExists("wisher@test.dk");

        assertThat(emailExists).isTrue();
    }

    @Test
    void emailExistsShouldBeFalse() {
        boolean emailExists = repository.emailExists("nonExistent");

        assertThat(emailExists).isFalse();
    }

    @Test
    void registerUser() {
        // arrange
    }

    @Test
    void updateUser() {
    }

    @Test
    void changePassword() {
    }

    @Test
    void deleteUser() {
    }
}