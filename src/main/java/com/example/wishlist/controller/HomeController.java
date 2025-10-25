package com.example.wishlist.controller;


import com.example.wishlist.utils.SessionUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String showHomePage(HttpSession session, Model model) {
        return "index";
    }
}

