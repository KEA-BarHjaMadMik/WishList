package com.example.wishlist.advice;

import com.example.wishlist.utils.SessionUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// provides global model attributes to all controllers
@ControllerAdvice
public class GlobalModelAttributes {

    // Adds 'isLoggedIn' and 'user' attributes to the model for all views
    @ModelAttribute
    public void addGlobalAttributes(HttpSession session, Model model) {
        boolean isLoggedIn = SessionUtils.isLoggedIn(session);
        model.addAttribute("isLoggedIn", isLoggedIn);

        if (isLoggedIn) {
            model.addAttribute("user", session.getAttribute("user"));
        }
    }
}
