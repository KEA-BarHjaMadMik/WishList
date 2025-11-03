package com.example.wishlist.controller;

import com.example.wishlist.model.User;
import com.example.wishlist.model.WishList;
import com.example.wishlist.service.WishListService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

@WebMvcTest(WishListController.class)
class WishListControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WishListService wishListService;

    @Mock
    private MockHttpSession mockHttpSession;

    private User user;
    private WishList wishList;
    private LocalDate localDate = LocalDate.parse("2030-12-12");
        //wishList = new WishList(1,"user", "test", "", localDate, false);

    @BeforeEach
    void setUp() {
        user = new User("tester", "test123", "test@gmail.com");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldGetWishLists() throws Exception{

    }



}
