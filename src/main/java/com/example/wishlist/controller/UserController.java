package com.example.wishlist.controller;

import com.example.wishlist.model.User;
import com.example.wishlist.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public String showLoginForm(HttpSession session) {
        // if already logged in, return to front page, else proceed to form
        return isLoggedIn(session) ? "index" : "login";
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

    @GetMapping("logout")
    public String logout(HttpSession session) {
        // invalidate session and return landing page
        session.invalidate();
        return "index";
    }

    @GetMapping("register_user")
    public String showRegistrationForm(HttpSession session, Model model) {
        // if already logged in, return to front page, else proceed to form
        if (isLoggedIn(session)) {
            return "index";
        }

        model.addAttribute("user", new User());
        return "user_registration_form";
    }


    @PostMapping("/register_user")
    public String registerUser(@Valid @ModelAttribute User user,
                               BindingResult bindingResult,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model) {

        // Check for field validation errors
        boolean fieldsHaveErrors = bindingResult.hasErrors();

        // Check if username is free
        boolean usernameTaken = service.usernameExists(user.getUsername());
        if (usernameTaken) {
            model.addAttribute("usernameTaken", true);
        }

        // Check if email is free
        boolean emailTaken = service.emailExists(user.getEmail());
        if (emailTaken) {
            model.addAttribute("emailTaken", true);
        }

        // Check if passwords match
        boolean passwordMismatch = !user.getPassword().equals(confirmPassword);
        if (passwordMismatch) {
            model.addAttribute("passwordMismatch", true);
        }

        // If validation failed, return to form
        if (fieldsHaveErrors || usernameTaken || emailTaken || passwordMismatch) {
            return "user_registration_form";
        }

        // Proceed with saving the user
        if (service.registerUser(user)) {
            return "redirect:/login";
        } else {
            model.addAttribute("registrationFailure", true);
            return "user_registration_form";
        }
    }

}
