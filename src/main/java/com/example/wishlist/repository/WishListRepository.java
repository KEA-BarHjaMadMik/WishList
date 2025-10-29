package com.example.wishlist.repository;

import com.example.wishlist.model.WishItem;
import com.example.wishlist.model.WishList;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.LocalDate;
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
                rs.getDate("eventdate") != null ? rs.getDate("eventdate").toLocalDate() : null,
                rs.getBoolean("not_public")));
    }

    public WishList getWishList(String wishListId) {
        String sql = "SELECT id, username, title, description, eventdate, not_public FROM wish_list WHERE id = ?";

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
            return jdbcTemplate.query(sql, rowMapper, wishListId);
        } catch (DataAccessException e) {
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

    public int createWishListAndReturnId(WishList wishList) {
        String sql = "INSERT INTO wish_list (username, title, description, eventdate, not_public) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

                        // Required fields
                        ps.setString(1, wishList.getUsername());
                        ps.setString(2, wishList.getTitle());

                        // Optional fields
                        String description = wishList.getDescription();
                        if (description != null) {
                            ps.setString(3, description);
                        } else {
                            ps.setNull(3, Types.VARCHAR);
                        }

                        LocalDate eventdate = wishList.getEventDate();
                        if (eventdate != null) {
                            ps.setDate(4, Date.valueOf(eventdate));
                        } else {
                            ps.setNull(4, Types.DATE);
                        }

                        ps.setBoolean(5, wishList.isNotPublic());

                        return ps;
                    },
                    keyHolder
            );

            Number wishListId = keyHolder.getKey();
            return (wishListId != null) ? wishListId.intValue() : -1;

        }catch (DataAccessException e) {
            System.err.println("Database error during wish list creation: " + e.getMessage());
            return -1;
        }
    }
}
