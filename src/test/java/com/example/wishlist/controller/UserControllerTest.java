package com.example.wishlist.controller;

import com.example.wishlist.model.User;
import com.example.wishlist.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldCreateUser() throws Exception {
        when(userService.registerUser(any(User.class))).thenReturn(true);

        mockMvc.perform(post("/register_user")
                        .param("username", "tester")
                        .param("email", "testmail@testers.com")
                        .param("password", "testing123")
                        .param("confirmPassword", "testing123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).registerUser(captor.capture());

        User captured = captor.getValue();
        assertEquals("tester", captured.getUsername());
        assertEquals("testmail@testers.com", captured.getEmail());
        assertEquals("testing123", captured.getPassword());
    }
}
