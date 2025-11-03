package com.example.wishlist.controller;

import com.example.wishlist.model.User;
import com.example.wishlist.model.WishList;
import com.example.wishlist.service.WishListService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishListController.class)
class WishListControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WishListService wishListService;

    private User user;
    private List<WishList> wishList;
    private LocalDate eventDate;

    @BeforeEach
    void setUp() {
        user = new User("tester", "test123", "test@gmail.com");
        eventDate = LocalDate.parse("2030-12-12");
        wishList.add(new WishList(1, "tester", "test", "1", eventDate, false));
        wishList.add(new WishList(2, "tester", "test2", "2", eventDate, false));

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldGetUserWishLists() throws Exception {
        when(wishListService.getUserWishLists("tester")).thenReturn(wishListService.getUserWishLists("tester"));

        mockMvc.perform(get("/wish_lists"))
                .andExpect(status().isOk())
                .andExpect(view().name("wish_lists"))
                .andExpect(model().attributeExists("userWishLists"));

        verify(wishListService).getUserWishLists("tester");
    }



}
