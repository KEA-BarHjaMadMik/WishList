USE wishlist_db;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE user_account;
TRUNCATE TABLE wish_list;
TRUNCATE TABLE wish_item;
SET FOREIGN_KEY_CHECKS = 1;

-- creating test users
INSERT INTO user_account
VALUES
    ('test_wisher', 'test123', 'wisher@test.dk'),
    ('test_wishee', 'test123', 'wishee@test.dk');

-- creating wish lists
INSERT INTO wish_list (username, title)
VALUES
    ('test_wisher','simpel');

INSERT INTO wish_list (username, title, description, eventdate)
VALUES (
           'test_wisher',
           'komplet',
           'ønskeseddel til fødselsdag',
           '2025-12-24'
       );

-- creating wishes in wish list 1
INSERT INTO wish_item (wish_list_id, title) VALUES (1,'Verdensfred');
INSERT INTO wish_item (wish_list_id, title, favourite, description, price, quantity, link, reserved, reserved_by)
    VALUES (
        1,
        'Fjernstyret bil',
        1,
        'bil der kan fjernstyres - SKAL være blå',
        239.00,
        1,
        'https://www.avxperten.dk/maxlife-rc-fjernstyret-bil-mxrc-300.asp',
        1,
        'test_wishee'
    );

-- creating wishes in wish list 2
INSERT INTO wish_item (wish_list_id, title) VALUES (2,'Verdensfred');
INSERT INTO wish_item (wish_list_id, title, favourite, description, price, quantity, link, reserved, reserved_by)
    VALUES (
               2,
               'Fjernstyret bil',
               1,
               'bil der kan fjernstyres - SKAL være blå',
               239.00,
               1,
               'https://www.avxperten.dk/maxlife-rc-fjernstyret-bil-mxrc-300.asp',
               1,
               'test_wishee'
    );
