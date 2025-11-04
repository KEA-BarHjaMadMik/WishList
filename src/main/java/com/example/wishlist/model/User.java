package com.example.wishlist.model;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class User {
    @Size(min=1, max=15, message = "Brugernavn skal være mellem 1 og 15 tegn")
    private String username;

    @Size(min = 6, message = "Password skal være mindst 6 tegn")
    private String password;

    @Email(message = "Ugyldig e-mailadresse")
    @NotBlank(message = "E-mail må ikke være tom")
    private String email;

    public User(){}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
