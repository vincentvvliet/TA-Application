package nl.tudelft.sem.User.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    public void testNotNull() {
        User user = new User();
        Assertions.assertNotNull(user);
    }
}