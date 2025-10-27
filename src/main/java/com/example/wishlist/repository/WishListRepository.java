package com.example.wishlist.repository;

import com.example.wishlist.model.WishList;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WishListRepository {
    private final JdbcTemplate jdbcTemplate;

    public WishListRepository(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<WishList> getUserWishLists(String username){
        String sql = "SELECT id, title, description, eventdate, not_public FROM wish_list WHERE username = ?";

        RowMapper<WishList> rowMapper = getWishListRowMapper();

        try{
            return jdbcTemplate.query(sql, rowMapper, username);
        } catch (DataAccessException e) {
            System.err.println("Database error during wish list query: " + e.getMessage());
            return null;
        }
    }

    private RowMapper<WishList> getWishListRowMapper(){
        return ((rs, rowNum) -> new WishList(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getDate("eventdate").toLocalDate(),
                rs.getBoolean("not_public")));
    }
}
