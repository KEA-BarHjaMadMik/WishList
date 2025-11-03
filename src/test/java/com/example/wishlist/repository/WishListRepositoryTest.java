package com.example.wishlist.repository;

import com.example.wishlist.model.WishItem;
import com.example.wishlist.model.WishList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:h2init.sql", executionPhase = BEFORE_TEST_METHOD)
class WishListRepositoryTest {

    @Autowired
    private WishListRepository repository;

    @Test
    void shouldGetUserWishLists() {
        String username = "test_wisher";

        List<WishList> wishLists = repository.getUserWishLists(username);

        assertThat(wishLists).isNotNull();

        // test size
        assertThat(wishLists.size()).isGreaterThanOrEqualTo(2);

        // test id
        assertThat(wishLists.get(0).getId()).isEqualTo(1);
        assertThat(wishLists.get(1).getId()).isEqualTo(2);

        // test title
        assertThat(wishLists.get(0).getTitle()).isEqualTo("simpel");
        assertThat(wishLists.get(1).getTitle()).isEqualTo("komplet");
        assertThat(wishLists.get(2).getTitle()).isEqualTo("privat");

        // test username is correct for all
        for(WishList wishList : wishLists){
            assertThat(wishList.getUsername()).isEqualTo(username);
        }

        // test description
        assertThat(wishLists.get(0).getDescription()).isNull();
        assertThat(wishLists.get(1).getDescription()).isNotNull();

        // test eventdate
        assertThat(wishLists.get(0).getEventDate()).isNull();
        assertThat(wishLists.get(1).getEventDate()).isEqualTo(LocalDate.of(2025, 12, 24));

        // test notPublic
        assertThat(wishLists.get(0).isNotPublic()).isFalse();
        assertThat(wishLists.get(1).isNotPublic()).isFalse();
        assertThat(wishLists.get(2).isNotPublic()).isTrue();
    }

    @Test
    void shouldGetEmptyUserWishLists() {
        String username = "test_wishee";

        List<WishList> wishLists = repository.getUserWishLists(username);

        assertThat(wishLists).isNotNull();

        // test size
        assertThat(wishLists).isEmpty();
    }

    @Test
    void shouldGetWishList() {
        WishList wishList = repository.getWishList("1");

        assertThat(wishList).isNotNull();
        assertThat(wishList.getTitle()).isEqualTo("simpel");
    }

    @Test
    void shouldGetNullWishList() {
        WishList wishList = repository.getWishList("0");

        assertThat(wishList).isNull();
    }

    @Test
    void shouldGetWishListItems() {
        List<WishItem> wishItems = repository.getWishListItems("1");

        assertThat(wishItems).isNotNull();

        assertThat(wishItems).isNotEmpty();

        assertThat(wishItems.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldGetEmptyWishListItems() {
        List<WishItem> wishItems = repository.getWishListItems("0");

        assertThat(wishItems).isNotNull();
        assertThat(wishItems).isEmpty();
    }
}
