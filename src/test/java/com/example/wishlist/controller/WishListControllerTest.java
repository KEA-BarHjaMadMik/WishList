package com.example.wishlist.controller;

import com.example.wishlist.model.User;
import com.example.wishlist.model.WishItem;
import com.example.wishlist.model.WishList;
import com.example.wishlist.service.WishListService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        wishList.add(new WishList(2, "tester", "test2", "2", eventDate, true));
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

    @Test
    void shouldRegisterWishList() throws Exception {
        when(wishListService.createWishListAndReturnId(any(WishList.class))).thenReturn(1);

        mockMvc.perform(post("/create_wish_list")
                        .param("title", "test3")
                        .param("description", "3")
                        .param("eventDate", "2030-12-12")
                        .param("notPublic", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/wish_list/" + 1));

        ArgumentCaptor<WishList> captor = ArgumentCaptor.forClass(WishList.class);
        verify(wishListService).createWishListAndReturnId(captor.capture());

        WishList captured = captor.getValue();
        assertEquals("test3", captured.getTitle());
        assertEquals("3", captured.getDescription());
        assertEquals(LocalDate.of(2030, 12, 12), captured.getEventDate());
        assertFalse((captured.isNotPublic()));
    }

    @Test
    void shouldGetWishList() throws Exception {
        when(wishListService.getWishList("1")).thenReturn(wishList.getFirst());

        mockMvc.perform(get("/wish_list/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("wish_list"))
                .andExpect(model().attributeExists("wishList"))
                .andExpect(model().attributeExists("wishListItems"));

        verify(wishListService).getWishList("1");
    }

    @Test
    void shouldAddWishItem() throws Exception {
        mockMvc.perform(post("/wish_item_add_form")
                        .param("wishListId", "1")
                        .param("title", "test item")
                        .param("favourite", "false")
                        .param("description", "test item")
                        .param("price", "20.95")
                        .param("quantity", "1")
                        .param("link", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/wish_list/1"));

        ArgumentCaptor<WishItem> captor = ArgumentCaptor.forClass(WishItem.class);
        verify(wishListService).addWishItem(captor.capture());

        WishItem captured = captor.getValue();
        assertEquals(1, captured.getWishListId());
        assertEquals("test item", captured.getTitle());
        assertFalse(captured.isFavourite());
        assertEquals("test item", captured.getDescription());
        assertEquals(20.95, captured.getPrice());
        assertEquals(1, captured.getQuantity());
        assertEquals("", captured.getLink());
    }
}
