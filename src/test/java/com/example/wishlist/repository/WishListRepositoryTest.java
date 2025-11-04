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
        for (WishList wishList : wishLists) {
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

    @Test
    void shouldGetWishItem() {
        int wishItemId = 1;

        WishItem wishItem = repository.getWishItem(String.valueOf(wishItemId));

        assertThat(wishItem).isNotNull();
        assertThat(wishItem.getId()).isEqualTo(wishItemId);
    }

    @Test
    void shouldCreateWishListAndReturnId() {
        // Arrange
        WishList newWishList = new WishList();
        newWishList.setUsername("test_wisher");
        newWishList.setTitle("integration test wishlist");
        newWishList.setDescription("Created in integration test");
        newWishList.setEventDate(LocalDate.of(2031, 1, 1));
        newWishList.setNotPublic(false);

        // Act
        int generatedId = repository.createWishListAndReturnId(newWishList);

        // Assert
        assertThat(generatedId).isGreaterThan(0);

        // Verify that the wishlist exists in DB
        WishList fetched = repository.getWishList(String.valueOf(generatedId));
        assertThat(fetched).isNotNull();
        assertThat(fetched.getUsername()).isEqualTo("test_wisher");
        assertThat(fetched.getTitle()).isEqualTo("integration test wishlist");
        assertThat(fetched.getDescription()).isEqualTo("Created in integration test");
        assertThat(fetched.getEventDate()).isEqualTo(LocalDate.of(2031, 1, 1));
        assertThat(fetched.isNotPublic()).isFalse();
    }

    @Test
    void shouldReturnMinusOneWhenInsertFails() {
        WishList invalidWishList = new WishList();
        invalidWishList.setUsername("test_wisher");
        invalidWishList.setTitle(null); // required field missing

        int result = repository.createWishListAndReturnId(invalidWishList);

        assertThat(result).isEqualTo(-1);
    }

    @Test
    void shouldAddWishItem() {
        // Arrange
        WishItem newItem = new WishItem();
        newItem.setWishListId(1);
        newItem.setTitle("New Test Item");
        newItem.setFavourite(false);
        newItem.setDescription("Inserted via integration test");
        newItem.setPrice(199.99);
        newItem.setQuantity(2);
        newItem.setLink("https://example.com/test-item");
        newItem.setReserved(false);
        newItem.setReservedBy(null);

        // Act
        boolean success = repository.addWishItem(newItem);

        // Assert
        assertThat(success).isTrue();

        // Verify that the item was added to DB
        List<WishItem> wishItems = repository.getWishListItems("1");

        boolean found = wishItems.stream()
                .anyMatch(item ->
                        item.getWishListId() == 1 &&
                                item.getTitle().equals("New Test Item") &&
                                !item.isFavourite() &&
                                "Inserted via integration test".equals(item.getDescription()) &&
                                Math.abs(item.getPrice() - 199.99) < 0.001 &&
                                item.getQuantity() == 2 &&
                                item.getLink().equals("https://example.com/test-item") &&
                                !item.isReserved() &&
                                item.getReservedBy() == null
                );

        assertThat(found).isTrue();
    }

    @Test
    void shouldNotAddWishItemWhenWishListDoesNotExist() {
        // Arrange
        WishItem invalidItem = new WishItem();
        invalidItem.setWishListId(9999); // non-existent wish_list_id (violates FK)
        invalidItem.setTitle("Invalid Item");
        invalidItem.setFavourite(false);
        invalidItem.setDescription("Should fail due to invalid wish_list_id");
        invalidItem.setPrice(49.99);
        invalidItem.setQuantity(1);
        invalidItem.setLink("https://example.com/invalid");
        invalidItem.setReserved(false);
        invalidItem.setReservedBy(null);

        // Act
        boolean success = repository.addWishItem(invalidItem);

        // Assert
        assertThat(success).isFalse();

        // Verify that it was not inserted
        List<WishItem> wishItems = repository.getWishListItems("9999");
        assertThat(wishItems).isEmpty();
    }

    @Test
    void shouldUpdateWishList() {
        // Arrange
        WishList existing = repository.getWishList("1");
        assertThat(existing).isNotNull();

        existing.setTitle("Updated Title");
        existing.setDescription("This wish list was updated via integration test");
        existing.setEventDate(LocalDate.of(2031, 1, 1));
        existing.setNotPublic(true);

        // Act
        boolean success = repository.updateWishList(existing);

        // Assert
        assertThat(success).isTrue();

        // Verify the data actually changed in DB
        WishList updated = repository.getWishList("1");

        assertThat(updated).isNotNull();
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getDescription()).isEqualTo("This wish list was updated via integration test");
        assertThat(updated.getEventDate()).isEqualTo(LocalDate.of(2031, 1, 1));
        assertThat(updated.isNotPublic()).isTrue();
    }

    @Test
    void shouldNotUpdateNonexistentWishList() {
        // Arrange
        WishList nonExisting = new WishList();
        nonExisting.setId(9999); // ID that doesn't exist in DB
        nonExisting.setTitle("This should not be updated");
        nonExisting.setDescription("Trying to update a non-existing wish list");
        nonExisting.setEventDate(LocalDate.of(2030, 1, 1));
        nonExisting.setNotPublic(true);

        // Act
        boolean success = repository.updateWishList(nonExisting);

        // Assert
        assertThat(success).isFalse();

        // Verify that it was not inserted
        WishList wishList = repository.getWishList("9999");
        assertThat(wishList).isNull();
    }

    @Test
    void shouldUpdateWishItem() {
        // Arrange
        WishItem existing = repository.getWishItem("1");
        assertThat(existing).isNotNull();

        existing.setTitle("Updated Title");
        existing.setFavourite(true);
        existing.setDescription("This wish item was updated via integration test");
        existing.setPrice(99.99);
        existing.setQuantity(2);
        existing.setLink("https://example.com/invalid");
        existing.setReserved(true);
        existing.setReservedBy("test_wishee");

        // Act
        boolean success = repository.updateWishItem(existing);

        // Assert
        assertThat(success).isTrue();

        // Verify the data actually changed in DB
        WishItem updated = repository.getWishItem("1");

        assertThat(updated).isNotNull();
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.isFavourite()).isTrue();
        assertThat(updated.getDescription()).isEqualTo("This wish item was updated via integration test");
        assertThat(updated.getPrice()).isEqualTo(99.99);
        assertThat(updated.getQuantity()).isEqualTo(2);
        assertThat(updated.getLink()).isEqualTo("https://example.com/invalid");
        assertThat(updated.isReserved()).isTrue();
        assertThat(updated.getReservedBy()).isEqualTo("test_wishee");
    }

    @Test
    void shouldNotUpdateNonexistentWishItem() {
        // Arrange
        WishItem nonExisting = new WishItem();

        nonExisting.setId(9999); // ID that doesn't exist in DB
        nonExisting.setWishListId(1);
        nonExisting.setTitle("This should not be updated");
        nonExisting.setFavourite(true);
        nonExisting.setDescription("Trying to update a non-existing wish list");
        nonExisting.setPrice(99.99);
        nonExisting.setQuantity(2);
        nonExisting.setLink("https://example.com/invalid");
        nonExisting.setReserved(true);
        nonExisting.setReservedBy("test_wishee");

        // Act
        boolean success = repository.updateWishItem(nonExisting);

        // Assert
        assertThat(success).isFalse();

        // Verify that it was not inserted
        WishItem wishItem = repository.getWishItem("9999");
        assertThat(wishItem).isNull();
    }

    @Test
    void shouldDeleteWishList() {
        // Act
        boolean success = repository.deleteWishList("1");

        // Assert
        assertThat(success).isTrue();

        // Verify the data actually changed in DB
        WishList wishList = repository.getWishList("1");
        assertThat(wishList).isNull();
    }

    @Test
    void shouldNotDeleteNonexistentWishList() {
        // Act
        boolean success = repository.deleteWishList("9999"); // ID that doesn't exist in DB

        // Assert
        assertThat(success).isFalse();
    }
}
