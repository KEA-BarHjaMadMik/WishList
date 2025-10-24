package com.example.wishlist.controller;

import com.example.wishlist.model.User;
import com.example.wishlist.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    private static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    @GetMapping("login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("login")
    public String login(@RequestParam("uid") String uid, @RequestParam("pw") String pw,
                        HttpSession session,
                        Model model) {

        // Attempt to authenticate the user
        User user = service.authenticate(uid, pw);

        if (user != null) {
            // Login successful — store the username in the session
            session.setAttribute("user", user.getUsername());
            // redirect
            return "redirect:/";
        }

        // Login failed — add an attribute to indicate incorrect credentials
        model.addAttribute("wrongCredentials", true);

        // Return to the login page
        return "login";
    }
}
