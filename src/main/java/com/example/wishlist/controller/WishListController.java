package com.example.wishlist.controller;

import com.example.wishlist.model.WishItem;
import com.example.wishlist.model.WishList;
import com.example.wishlist.service.WishListService;
import com.example.wishlist.utils.SessionUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class WishListController {
    private final WishListService service;

    public WishListController(WishListService service) {
        this.service = service;
    }

    @GetMapping("/wish_lists")
    public String getUserWishLists(HttpSession session, Model model) {
        // Ensure user is logged in
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        String username = (String) session.getAttribute("username");

        List<WishList> userWishLists = service.getUserWishLists(username);
        if (userWishLists != null) {
            model.addAttribute("userWishLists", userWishLists);
        } else {
            model.addAttribute("queryFailure", true);
        }

        return "wish_lists";
    }

    @GetMapping("/wish_list/{wishListId}")
    public String getWishList(@PathVariable String wishListId, Model model) {
        // Get wish list and items
        WishList wishList = service.getWishList(wishListId);
        List<WishItem> wishListItems = service.getWishListItems(wishListId);

        // if successful, add to model
        if (wishList != null && wishListItems != null) {
            model.addAttribute("wishList", wishList);
            model.addAttribute("wishListItems", wishListItems);
        } else {
            model.addAttribute("queryFailure", true);
        }

        return "wish_list";
    }

    @GetMapping("/create_wish_list")
    public String showCreateWishListForm(HttpSession session, Model model) {
        // Ensure user is logged in
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        model.addAttribute("wishList", new WishList());
        return "wish_list_registration_form";
    }

    @PostMapping("/create_wish_list")
    public String createWishList(HttpSession session,
                                 @Valid @ModelAttribute WishList wishList,
                                 BindingResult bindingResult,
                                 Model model) {

        // Ensure user is logged in
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        // Set wish list username to current user
        String username = (String) session.getAttribute("username");
        wishList.setUsername(username);

        // Check for field validation errors
        boolean fieldsHaveErrors = bindingResult.hasErrors();

        // If validation failed, return to form
        if (fieldsHaveErrors) {
            return "wish_list_registration_form";
        }

        // Proceed with registration
        int wishListId  = service.createWishListAndReturnId(wishList);
        if (wishListId != -1) {
            return "redirect:/wish_list/" + wishListId;
        } else {
            model.addAttribute("registrationFailure", true);
            return "wish_list_registration_form";
        }
    }

    @GetMapping("/wish_item/{wishItemId}")
    public String getWishItem(@PathVariable String wishItemId, Model model){
        WishItem wishItem = service.getWishItem(wishItemId);
        WishList wishList;

        //checks if there is an item or not
        if (wishItem != null) {
            wishList = service.getWishList(String.valueOf(wishItem.getWishListId()));
        } else {
            model.addAttribute("queryFailure", true);
            return "wish_item";
        }

        if (wishList != null){
            model.addAttribute("wishItem", wishItem);
            model.addAttribute("wishList", wishList);
        } else {
            model.addAttribute("queryFailure", true);
        }

        return "wish_item";
    }
}
