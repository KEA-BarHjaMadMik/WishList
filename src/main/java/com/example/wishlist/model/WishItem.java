package com.example.wishlist.model;

public class WishItem {
    private int id;
    private String title;
    private boolean favourite;
    private String description;
    private double price;
    private int quantity;
    private String link;
    private boolean reserved;
    private String reservedBy;

    public WishItem(){}

    public WishItem(int id, String title, boolean favourite, String description, double price, int quantity, String link, boolean reserved, String reservedBy) {
        this.id = id;
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
        this.reservedBy = reservedBy;
    }
}
