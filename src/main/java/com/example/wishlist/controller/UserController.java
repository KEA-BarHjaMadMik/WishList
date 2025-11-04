package com.example.wishlist.controller;

import com.example.wishlist.model.User;
import com.example.wishlist.service.UserService;
import com.example.wishlist.utils.SessionUtil;
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

    @GetMapping("/login")
    public String showLoginForm(HttpSession session) {
        // if already logged in, return to front page, else proceed to form
        return SessionUtil.isLoggedIn(session) ? "index" : "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("uid") String uid, @RequestParam("pw") String pw,
                        HttpSession session,
                        Model model) {

        // Attempt to authenticate the user
        User user = service.authenticate(uid, pw);

        if (user != null) {
            // Login successful — store the username in the session
            session.setAttribute("username", user.getUsername());
            // redirect
            return "redirect:/";
        }

        // Login failed — add an attribute to indicate incorrect credentials
        model.addAttribute("wrongCredentials", true);

        // Return to the login page
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // invalidate session and return landing page
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/register_user")
    public String showRegistrationForm(HttpSession session, Model model) {
        // if already logged in, return to front page, else proceed to form
        if (SessionUtil.isLoggedIn(session)) {
            return "index";
        }

        model.addAttribute("newUser", new User());
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

        // Proceed with saving the username
        if (service.registerUser(user)) {
            return "redirect:/login";
        } else {
            model.addAttribute("registrationFailure", true);
            return "user_registration_form";
        }
    }

    @GetMapping("/user_admin")
    public String showUserAdminPage(HttpSession session, Model model) {
        // Ensure user is logged in
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        // Retrieve username from session
        String username = (String) session.getAttribute("username");

        // Fetch full user object
        User user = service.getUserByUsername(username);

        // Add user to model
        model.addAttribute("user", user);

        return "user_admin";
    }

    @PostMapping("/update_user")
    public String updateUser(@Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             HttpSession session,
                             Model model) {
        // Ensure user is logged in
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        // Retrieve current username from session
        String currentUsername = (String) session.getAttribute("username");

        // Check for field validation errors (excluding password)
        boolean fieldsHaveErrors = bindingResult.hasFieldErrors("username") ||
                bindingResult.hasFieldErrors("email");

        // Check if the new username is taken (and not the current one)
        boolean usernameTaken = !user.getUsername().equals(currentUsername) &&
                service.usernameExists(user.getUsername());
        if (usernameTaken) {
            model.addAttribute("usernameTaken", true);
        }

        // Check if the new email is taken (and not the current one)
        boolean emailTaken = service.emailExists(user.getEmail()) &&
                !service.getUserByUsername(currentUsername).getEmail().equals(user.getEmail());
        if (emailTaken) {
            model.addAttribute("emailTaken", true);
        }

        // If validation failed, return to form
        if (fieldsHaveErrors || usernameTaken || emailTaken) {
            return "user_admin";
        }

        // Proceed with updating the user
        if (service.updateUser(currentUsername, user)) {
            // Update session if username changed
            session.setAttribute("username", user.getUsername());
            model.addAttribute("updateSuccess", true);
        } else {
            model.addAttribute("updateFailure", true);
        }

        return "redirect:/user_admin";
    }

    @GetMapping("/change_password")
    public String showChangePasswordForm(HttpSession session) {
        // Ensure user is logged in
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        return "change_password";
    }

    @PostMapping("/change_password")
    public String changePassword(@RequestParam("password") String password,
                             @RequestParam("newPassword") String newPassword,
                             @RequestParam("confirmNewPassword") String confirmNewPassword,
                             HttpSession session,
                             Model model) {

        // Retrieve username from session
        String username = (String) session.getAttribute("username");
        // Attempt to authenticate the user
        User user = service.authenticate(username, password);

        boolean incorrectPassword = user == null;
        if (incorrectPassword) {
            model.addAttribute("incorrectPassword", true);
        }

        boolean passwordTooShort = newPassword.length() < 6;
        if(passwordTooShort) {
            model.addAttribute("passwordTooShort", true);
        }

        // Check if passwords match
        boolean passwordMismatch = !newPassword.equals(confirmNewPassword);
        if (passwordMismatch) {
            model.addAttribute("passwordMismatch", true);
        }

        // If validation failed, return to form
        if (incorrectPassword || passwordTooShort || passwordMismatch) {
            return "change_password";
        }

        // Proceed with updating the password
        if (service.changePassword(username, newPassword)) {
            return "redirect:/user_admin";
        } else {
            model.addAttribute("updateFailure", true);
            return "change_password";
        }
    }

    @PostMapping("/delete")
    public String deleteUser(HttpSession session, Model model){
        // Ensure user is logged in
        if (!SessionUtil.isLoggedIn(session)) {
            return "redirect:/login";
        }

        // Retrieve username from session
        String username = (String) session.getAttribute("username");

        // Proceed with deleting user
        if(service.deleteUser(username)) {
            session.invalidate();
            return "redirect:/";
        }else{
            model.addAttribute("deleteFailure", true);
            return "user_admin";
        }
    }
}
