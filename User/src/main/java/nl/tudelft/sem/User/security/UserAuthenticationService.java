package nl.tudelft.sem.User.security;


import nl.tudelft.sem.User.entities.User;

import java.util.Optional;

public interface UserAuthenticationService {

    /**
     * Logs in with the given {@code username} and {@code password} and {@code role}.
     *
     * @param username
     * @param password
     * @return an {@link Optional} of a JWT token when login succeeds
     */
    Optional<String> login(String username, String password, String role);

    /**
     * Finds a user by its token.
     *
     * @param token user token
     * @return
     */
    Optional<User> findByToken(String token);

    /**
     * Logs out the given input {@code user}.
     *
     * @param user the user to logout
     */
    void logout(User user);
}
