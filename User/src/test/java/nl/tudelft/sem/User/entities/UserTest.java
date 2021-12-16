package nl.tudelft.sem.User.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    User emptyUser;
    User user;

    @BeforeEach
    public void setup() {
        emptyUser = new User();
        user = new User("username", "password", Role.valueOf("STUDENT"));
    }

    /**
     * Test not null.
     */
    @Test
    public void testNotNull() {
        Assertions.assertNotNull(emptyUser);
    }
}