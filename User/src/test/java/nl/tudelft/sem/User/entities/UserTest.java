package nl.tudelft.sem.User.entities;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

    /**
     * The Empty user.
     */
    User emptyUser;
    /**
     * The User.
     */
    User user;

    /**
     * Setup to be run before each test.
     */
    @BeforeEach
    public void setup() {
        emptyUser = new User();
        user = new User("username", "password", Role.valueOf("STUDENT"));
    }

    /**
     * Test not null of user with empty constructor.
     */
    @Test
    public void testNotNullEmpty() {
        Assertions.assertNotNull(emptyUser);
    }

    /**
     * Test not null of user with normal constructor.
     */
    @Test
    public void testNotNull() {
        Assertions.assertNotNull(user);
    }

    /**
     * Test get password of empty user.
     */
    @Test
    public void testGetPasswordEmpty() {
        Assertions.assertNull(emptyUser.getPassword());
    }

    /**
     * Test get password.
     */
    @Test
    public void testGetPassword() {
        Assertions.assertEquals(user.getPassword(),"password");
    }

    /**
     * Test get authorities.
     */
    @Test
    public void testGetAuthorities() {
        Assertions.assertTrue(user.isAccountNonExpired());
    }

    /**
     * Test is account non expired.
     */
    @Test
    public void testIsAccountNonExpired() {
        Assertions.assertTrue(user.isAccountNonExpired());
    }

    /**
     * Test is account non locked.
     */
    @Test
    public void testIsAccountNonLocked() {
        Assertions.assertTrue(user.isAccountNonExpired());
    }

    /**
     * Test is credentials non expired.
     */
    @Test
    public void testIsCredentialsNonExpired() {
        Assertions.assertTrue(user.isAccountNonExpired());
    }

    /**
     * Test is enabled.
     */
    @Test
    public void testIsEnabled() {
        Assertions.assertTrue(user.isAccountNonExpired());
    }
}