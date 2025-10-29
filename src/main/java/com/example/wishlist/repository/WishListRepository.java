package com.example.wishlist.repository;

import com.example.wishlist.model.WishItem;
import com.example.wishlist.model.WishList;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WishListRepository {
    private final JdbcTemplate jdbcTemplate;

    public WishListRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<WishList> getUserWishLists(String username) {
        String sql = "SELECT id, username, title, description, eventdate, not_public FROM wish_list WHERE username = ?";

        RowMapper<WishList> rowMapper = getWishListRowMapper();

        try {
            return jdbcTemplate.query(sql, rowMapper, username);
        } catch (DataAccessException e) {
            System.err.println("Database error during wish list query: " + e.getMessage());
            return null;
        }
    }

    private RowMapper<WishList> getWishListRowMapper() {
        return ((rs, rowNum) -> new WishList(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getDate("eventdate").toLocalDate(),
                rs.getBoolean("not_public")));
    }

    public WishList getWishList(String wishListId) {
        String sql = "SELECT id, title, description, eventdate, not_public FROM wish_list WHERE id = ?";

        RowMapper<WishList> rowMapper = getWishListRowMapper();

        try {
            List<WishList> results = jdbcTemplate.query(sql, rowMapper, wishListId);
            return results.isEmpty() ? null : results.getFirst();
        } catch (DataAccessException e) {
            System.err.println("Database error during wish list query: " + e.getMessage());
            return null;
        }
    }

    public List<WishItem> getWishListItems(String wishListId) {
        String sql = "SELECT id, title, favourite, description, price, quantity, link, reserved, reserved_by FROM wish_item WHERE wish_list_id = ?";

        RowMapper<WishItem> rowMapper = getWishItemRowMapper();

        try {
            return jdbcTemplate.query(sql, rowMapper,wishListId);
        } catch (DataAccessException e){
            System.err.println("Database error during wish list query: " + e.getMessage());
            return null;
        }
    }

    private RowMapper<WishItem> getWishItemRowMapper() {
        return ((rs, rowNum) -> new WishItem(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getBoolean("favourite"),
                rs.getString("description"),
                rs.getDouble("price"),
                rs.getInt("quantity"),
                rs.getString("link"),
                rs.getBoolean("reserved"),
                rs.getString("reserved_by")));
    }
}
