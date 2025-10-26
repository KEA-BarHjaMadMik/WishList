package com.example.wishlist.utils;

import jakarta.servlet.http.HttpSession;

public class SessionUtils {

    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("username") != null;
    }
}
