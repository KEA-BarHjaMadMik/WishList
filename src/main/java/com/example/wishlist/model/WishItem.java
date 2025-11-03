package com.example.wishlist.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class WishItem {
    private int id;
    private int wishListId;

    @NotBlank(message = "Titel må ikke være tom.")
    @Size(max = 100, message = "Titel kan højest være 100 tegn.")
    private String title;
    private boolean favourite;

    @Size(max = 800, message = "Beskrivelse kan højest være 800 tegn.")
    private String description;

    @DecimalMin(value = "0.0", message = "Pris skal være et positivt tal.")
    private double price;

    @Min(value = 1, message = "Antal skal være mindst 1.")
    private int quantity;

    @Size(max = 400, message = "Link kan højest være 400 tegn.")
    private String link;

    private boolean reserved;
    private String reservedBy;
    public WishItem(){
        this.quantity = 1; // sets item quantity to 1 globally
    }

    public WishItem(int id, int wishListId, String title, boolean favourite, String description, double price, int quantity, String link, boolean reserved, String reservedBy) {
        this.id = id;
        this.wishListId = wishListId;
        this.title = title;
        this.favourite = favourite;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.link = link;
        this.reserved = reserved;
        this.reservedBy = reservedBy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWishListId(){
        return wishListId;
    }

    public void setWishListId(int wishListId){
        this.wishListId = wishListId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public String getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(String reservedBy) {
        // Converts empty string to null to satisfy foreign key constraint
        this.reservedBy = (reservedBy != null && reservedBy.trim().isEmpty()) ? null : reservedBy;
    }
}
