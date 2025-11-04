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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class WishListController {
    private final WishListService service;

    public WishListController(WishListService service) {
        this.service = service;
    }

    @GetMapping("/wish_lists")
    public String getUserWishLists(HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";

        String username = getCurrentUsername(session);
        List<WishList> userWishLists = service.getUserWishLists(username);

        if (userWishLists != null) {
            model.addAttribute("userWishLists", userWishLists);
        } else {
            model.addAttribute("queryFailure", true);
        }

        return "wish_lists";
    }

    @GetMapping("/wish_list/{wishListId}")
    public String getWishList(@PathVariable String wishListId, HttpSession session, Model model) {
        // Get wish list and items
        WishList wishList = service.getWishList(wishListId);
        List<WishItem> wishListItems = service.getWishListItems(wishListId);
        // Add session info and ownership status to model
        addOwnershipToModel(session, model, wishList);

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
    public String getWishItem(@PathVariable String wishItemId, HttpSession session, Model model) {
        WishItem wishItem = service.getWishItem(wishItemId);

        //checks if there is an item or not
        if (wishItem == null) {
            model.addAttribute("queryFailure", true);
            return "wish_item";
        }

        WishList wishList = service.getWishList(String.valueOf(wishItem.getWishListId()));

        if (wishList != null) {
            model.addAttribute("wishItem", wishItem);
            model.addAttribute("wishList", wishList);
            addOwnershipToModel(session, model, wishList);
        } else {
            model.addAttribute("queryFailure", true);
        }

        return "wish_item";
    }

    @GetMapping("/create_wish_list")
    public String showCreateWishListForm(HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";

        model.addAttribute("wishList", new WishList());
        return "wish_list_registration_form";
    }

    @PostMapping("/create_wish_list")
    public String createWishList(HttpSession session,
                                 @Valid @ModelAttribute WishList wishList,
                                 BindingResult bindingResult,
                                 Model model) {

        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";

        // Set wish list username to current user
        wishList.setUsername(getCurrentUsername(session));

        // If validation failed, return to form
        if (bindingResult.hasErrors()) {
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

    @GetMapping("/wish_list/{wishListId}/add_wish_item")
    public String showAddWishListForm(@PathVariable String wishListId, HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";

        WishList wishList = getWishListIfOwner(wishListId, session);
        if (wishList == null) {
            return "redirect:/";
        }

        // Add a blank WishItem to the model
        WishItem wishItem = new WishItem();
        wishItem.setWishListId(Integer.parseInt(wishListId));
        model.addAttribute("wishItem", wishItem);
        model.addAttribute("wishList", wishList);

        return "wish_item_add_form";
    }

    @PostMapping("/wish_list/{wishListId}/add_wish_item")
    public String addWishItem(@PathVariable String wishListId,
                              HttpSession session,
                              @Valid @ModelAttribute WishItem wishItem,
                              BindingResult bindingResult,
                              Model model) {

        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";

        WishList wishList = getWishListIfOwner(wishListId, session);
        if (wishList == null) {
            return "redirect:/";
        }

        // If validation failed, return to form
        if (bindingResult.hasErrors()) {
            model.addAttribute("wishList", wishList);
            return "wish_item_add_form";
        }

        boolean success = service.addWishItem(wishItem);

        if (success) {
            return "redirect:/wish_list/" + wishListId;
        } else {
            model.addAttribute("saveFailure", true);
            model.addAttribute("wishList", wishList);
            return "wish_item_add_form";
        }
    }

    @GetMapping("/edit_wish_list/{wishListId}")
    public String showEditWishListForm(@PathVariable String wishListId, HttpSession session, Model model) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";

        WishList wishList = getWishListIfOwner(wishListId, session);
        if (wishList == null) {
            return "redirect:/";
        }

        model.addAttribute("wishList", wishList);
        return "wish_list_edit_form";
    }

    @PostMapping("/edit_wish_list")
    public String updateWishList(HttpSession session,
                                 @Valid @ModelAttribute WishList wishList,
                                 BindingResult bindingResult,
                                 Model model) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";

        if (!isOwner(session, wishList)) {
            return "redirect:/";
        }

        // If validation failed, return to form
        if (bindingResult.hasErrors()) {
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

    @GetMapping("/edit_wish_item/{wishItemId}")
    public String showEditWishItemForm(@PathVariable String wishItemId,
                                       HttpSession session,
                                       Model model) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";

        WishItem wishItem = service.getWishItem(wishItemId);
        if (wishItem == null) {
            model.addAttribute("queryFailure", true);
            return "wish_item_edit_form";
        }

        WishList wishList = getWishListIfOwner(String.valueOf(wishItem.getWishListId()), session);
        if (wishList == null) {
            return "redirect:/";
        }

        model.addAttribute("wishItem", wishItem);
        return "wish_item_edit_form";
    }

    @PostMapping("/edit_wish_item")
    public String updateWishItem(HttpSession session,
                                 @Valid @ModelAttribute WishItem wishItem,
                                 BindingResult bindingResult,
                                 Model model) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";

        WishList wishList = getWishListIfOwner(String.valueOf(wishItem.getWishListId()), session);
        if (wishList == null) {
            return "redirect:/";
        }

        // If validation failed, return to form
        if (bindingResult.hasErrors()) {
            model.addAttribute("wishItem", wishItem);
            return "wish_item_edit_form";
        }

        // Proceed with update
        if (service.updateWishItem(wishItem)) {
            return "redirect:/wish_list/" + wishItem.getWishListId();
        } else {
            model.addAttribute("wishItem", wishItem);
            model.addAttribute("updateFailure", true);
            return "wish_item_edit_form";
        }
    }

    @PostMapping("/delete_wish_list/{wishListId}")
    public String deleteWishList(HttpSession session,
                                 @PathVariable String wishListId,
                                 RedirectAttributes redirectAttributes) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";

        WishList wishList = getWishListIfOwner(wishListId, session);
        if (wishList == null) {
            return "redirect:/";
        }

        // Proceed with deletion
        if (service.deleteWishList(wishListId)) {
            return "redirect:/wish_lists";
        } else {
            redirectAttributes.addFlashAttribute("deleteFailure", true);
            return "redirect:/edit_wish_list/" + wishList.getId();
        }
    }

    @PostMapping("/delete_wish_item/{wishItemId}")
    public String deleteWishItem(HttpSession session, @PathVariable String wishItemId, Model model) {
        if (!SessionUtil.isLoggedIn(session)) return "redirect:/login";

        WishItem wishItem = service.getWishItem(wishItemId);
        if (wishItem == null) return "redirect:/";

        int wishListId = wishItem.getWishListId();
        WishList wishList = service.getWishList(String.valueOf(wishListId));

        if (!isOwner(session, wishList)) {
            return "redirect:/";
        }

        if (service.deleteWishItem(wishItemId)) {
            return "redirect:/wish_list/" + wishListId;
        } else {
            model.addAttribute("deleteFailure", true);
            return "wish_list";
        }
    }

    // Helper methods for authentication
    // Checks if user is logged in, returns redirect if not
//    private String requireLogin(HttpSession session) {
//        return SessionUtil.isLoggedIn(session) ? null : "redirect:/login";
//    }

    //Gets the current logged-in username from session
    private String getCurrentUsername(HttpSession session) {
        return (String) session.getAttribute("username");
    }

    //Checks if the current user owns the given wish list
    private boolean isOwner(HttpSession session, WishList wishList) {
        return wishList != null && getCurrentUsername(session).equals(wishList.getUsername());
    }

    // Fetches a wish list and verifies the current user owns it
    private WishList getWishListIfOwner(String wishListId, HttpSession session) {
        WishList wishList = service.getWishList(wishListId);
        return isOwner(session, wishList) ? wishList : null;
    }

    // Adds session info and ownership status to the model for view permission checks
//    private void addSessionAndOwnershipToModel(HttpSession session, Model model, WishList wishList) {
//        boolean isLoggedIn = SessionUtil.isLoggedIn(session);
//        model.addAttribute("isLoggedIn", isLoggedIn);
//
//        if (isLoggedIn) {
//            String username = getCurrentUsername(session);
//            model.addAttribute("username", username);
//            model.addAttribute("isOwner", wishList != null && username.equals(wishList.getUsername()));
//        } else {
//            model.addAttribute("isOwner", false);
//        }
//    }

    // Adds only ownership info to model since isLoggedIn and username are global.
    private void addOwnershipToModel(HttpSession session, Model model, WishList wishList) {
        String username = getCurrentUsername(session);
        boolean isOwner = wishList != null && username != null && username.equals(wishList.getUsername());
        model.addAttribute("isOwner", isOwner);
    }

}
