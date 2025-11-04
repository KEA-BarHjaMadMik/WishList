package com.example.wishlist.controller;

import com.example.wishlist.model.WishItem;
import com.example.wishlist.model.WishList;
import com.example.wishlist.service.WishListService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

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

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("username", "test_wisher");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldGetUserWishLists() throws Exception {
        mockMvc.perform(get("/wish_lists")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("wish_lists"))
                .andExpect(model().attributeExists("userWishLists"));

        verify(wishListService, times(1)).getUserWishLists("test_wisher");
    }

    @Test
    void shouldRegisterWishList() throws Exception {
        when(wishListService.createWishListAndReturnId(any(WishList.class))).thenReturn(1);

        mockMvc.perform(post("/create_wish_list")
                        .session(session)
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
        WishList userWishList = new WishList(1,
                "test_wisher",
                "test",
                "1",
                LocalDate.of(2030, 12, 12),
                false);
        when(wishListService.getWishList("1")).thenReturn(userWishList);

        mockMvc.perform(get("/wish_list/" + userWishList.getId())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("wish_list"))
                .andExpect(model().attributeExists("wishList"))
                .andExpect(model().attributeExists("wishListItems"));

        verify(wishListService).getWishList("1");
    }

    @Test
    void shouldAddWishItem() throws Exception {
        String wishListId = "1";
        WishList wishList = new WishList();
        wishList.setId(10);
        wishList.setUsername("test_wisher");

        when(wishListService.getWishList(wishListId)).thenReturn(wishList);
        when(wishListService.addWishItem(any(WishItem.class))).thenReturn(true);

        mockMvc.perform(post("/wish_list/{wishListId}/add_wish_item", wishListId)
                        .session(session)
                        .param("wishListId", "1")
                        .param("title", "test item")
                        .param("favourite", "false")
                        .param("description", "test item")
                        .param("price", "20.95")
                        .param("quantity", "1")
                        .param("link", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/wish_list/" + wishListId));

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

    @Test
    void shouldRemoveWishItem() throws Exception {
        String wishItemId = "5";
        int wishListId = 10;

        WishItem wishItem = new WishItem();
        wishItem.setId(5);
        wishItem.setWishListId(wishListId);

        WishList wishList = new WishList();
        wishList.setId(wishListId);
        wishList.setUsername("test_wisher");

        when(wishListService.getWishItem(wishItemId)).thenReturn(wishItem);
        when(wishListService.getWishList(String.valueOf(wishListId))).thenReturn(wishList);
        when(wishListService.deleteWishItem(wishItemId)).thenReturn(true);

        mockMvc.perform(post("/delete_wish_item/{wishItemId}", wishItemId)
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/wish_list/" + wishListId));

        verify(wishListService, times(1)).deleteWishItem("5");
    }
}
