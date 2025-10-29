package com.example.wishlist.model;

import java.time.LocalDate;

public class WishList {
    private int id;
    private String username;
    private String title;
    private String description;
    private LocalDate eventDate;
    private boolean notPublic;

    public WishList(){}

    public WishList(int id, String username, String title, String description,
                    LocalDate eventDate, boolean notPublic) {
        this.id = id;
        this.username = username;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.notPublic = notPublic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public boolean isNotPublic() {
        return notPublic;
    }

    public void setNotPublic(boolean notPublic) {
        this.notPublic = notPublic;
    }
}
