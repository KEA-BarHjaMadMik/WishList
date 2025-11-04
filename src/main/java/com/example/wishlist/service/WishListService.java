package com.example.wishlist.service;

import com.example.wishlist.model.WishItem;
import com.example.wishlist.model.WishList;
import com.example.wishlist.repository.WishListRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishListService {
    private final WishListRepository repository;

    public WishListService(WishListRepository repository) {
        this.repository = repository;
    }

    public List<WishList> getUserWishLists(String username) {
        return repository.getUserWishLists(username);
    }

    public WishList getWishList(String wishListId) {
        return repository.getWishList(wishListId);
    }

    public List<WishItem> getWishListItems(String wishListId) {
        return repository.getWishListItems(wishListId);
    }

    public WishItem getWishItem(String wishItemId) {
        return repository.getWishItem(wishItemId);
    }

    public int createWishListAndReturnId(WishList wishList) {
        return repository.createWishListAndReturnId(wishList);
    }

    public boolean addWishItem(WishItem wishItem) {
        return repository.addWishItem(wishItem);
    }

    public boolean updateWishList(WishList wishList) {
        return repository.updateWishList(wishList);
    }

    public boolean updateWishItem(WishItem wishItem) {
        return repository.updateWishItem(wishItem);
    }

    public boolean deleteWishList(String wishListId) {
        return repository.deleteWishList(wishListId);
    }

    public boolean deleteWishItem(String wishItemId) {
        return repository.deleteWishItem(wishItemId);
    }
}
