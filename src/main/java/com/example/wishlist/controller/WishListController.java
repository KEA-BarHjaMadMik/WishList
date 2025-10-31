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

    @GetMapping("/wish_item/{wishItemId}")
    public String getWishItem(@PathVariable String wishItemId, Model model) {
        WishItem wishItem = service.getWishItem(wishItemId);
        WishList wishList;

        //checks if there is an item or not
        if (wishItem != null) {
            wishList = service.getWishList(String.valueOf(wishItem.getWishListId()));
        } else {
            model.addAttribute("queryFailure", true);
            return "wish_item";
        }

        if (wishList != null) {
            model.addAttribute("wishItem", wishItem);
            model.addAttribute("wishList", wishList);
        } else {
            model.addAttribute("queryFailure", true);
        }

        return "wish_item";
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
        int wishListId = service.createWishListAndReturnId(wishList);
        if (wishListId != -1) {
            return "redirect:/wish_list/" + wishListId;
        } else {
            model.addAttribute("registrationFailure", true);
            return "wish_list_registration_form";
        }
    }

    @GetMapping("/edit_wish_list/{wishListId}")
    public String showEditWishListForm(@PathVariable String wishListId, HttpSession session, Model model) {
        // Ensure user is logged in
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        // Get wish list and items
        WishList wishList = service.getWishList(wishListId);

        // if successful
        if (wishList != null) {
            // If not current user's wish list, redirect to front page
            String currentUser = (String) session.getAttribute("username");
            if (!currentUser.equals(wishList.getUsername())) {
                return "redirect:/";
            }
            // else add to model
            model.addAttribute("wishList", wishList);
        } else {
            model.addAttribute("queryFailure", true);
        }

        return "wish_list_edit_form";
    }

    @PostMapping("/edit_wish_list")
    public String updateWishList(HttpSession session,
                                 @Valid @ModelAttribute WishList wishList,
                                 BindingResult bindingResult,
                                 Model model) {
        // Ensure user is logged in
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        // If not current user's wish list, redirect to front page
        String currentUser = (String) session.getAttribute("username");
        if (!currentUser.equals(wishList.getUsername())) {
            return "redirect:/";
        }

        // Check for field validation errors
        boolean fieldsHaveErrors = bindingResult.hasErrors();

        // If validation failed, return to form
        if (fieldsHaveErrors) {
            return "wish_list_edit_form";
        }

        // Proceed with update
        if (service.updateWishList(wishList)) {
            return "redirect:/wish_list/" + wishList.getId();
        } else {
            model.addAttribute("updateFailure", true);
            return "wish_list_edit_form";
        }
    }

    @PostMapping("/delete_wish_list/{wishListId}")
    public String deleteWishList(HttpSession session, @PathVariable String wishListId, Model model) {
        // Ensure user is logged in
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        // Ensure current user owns wishList
        WishList wishList = service.getWishList(wishListId);
        if (!wishList.getUsername().equals(session.getAttribute("username"))) {
            return "redirect:/";
        }

        // Proceed with deletion
        if (service.deleteWishList(wishListId)){
            return "redirect:/wish_lists";
        } else {
            model.addAttribute("deleteFailure", true);
            return "redirect:/edit_wish_list/" + wishList.getId();
        }
    }

    @PostMapping("/delete_wish_item/{wishItemId}")
    public String deleteWishItem(HttpSession session, @PathVariable String wishItemId, Model model){
        // Ensure user is logged in
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        WishItem wishItem = service.getWishItem(wishItemId);
        int wishListId = wishItem.getWishListId();
        WishList wishList = service.getWishList(String.valueOf(wishListId));

        if (!session.getAttribute("username").equals(wishList.getUsername())) {
            return "redirect:/";
        }

        if (service.deleteWishItem(wishItemId)) {
            return "redirect:/wish_list/" + wishListId;
        } else {
            model.addAttribute("deleteFailure", true);
            return "wish_list";
        }
    }
}
