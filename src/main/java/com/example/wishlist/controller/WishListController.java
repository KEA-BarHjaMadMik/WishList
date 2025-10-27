package com.example.wishlist.controller;

import com.example.wishlist.model.WishList;
import com.example.wishlist.service.WishListService;
import com.example.wishlist.utils.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class WishListController {
    private final WishListService service;

    public WishListController(WishListService service) {
        this.service = service;
    }

    @GetMapping("/wish_lists")
    public String getUserWishLists(HttpSession session, Model model){
        // Ensure user is logged in
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        String username = (String) session.getAttribute("username");

        List<WishList> userWishLists = service.getUserWishLists(username);

        model.addAttribute("userWishLists",userWishLists);

        return "wish_lists";
    }

    @GetMapping("/wish_list/{wishListId}")
    public String getWishList(@PathVariable String wishListId, HttpSession session, Model model){
        // Ensure user is logged in
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        return "wish_list";
    }
}
