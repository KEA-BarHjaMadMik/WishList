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

    public int createWishListAndReturnId(WishList wishList) {
        return repository.createWishListAndReturnId(wishList);
    }

    public WishItem getWishItem(String wishItemId){
        return repository.getWishItem(wishItemId);
    }
}
