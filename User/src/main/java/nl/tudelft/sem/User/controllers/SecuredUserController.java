package nl.tudelft.sem.User.controllers;

import lombok.NonNull;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.UserRepository;
import nl.tudelft.sem.User.security.UserAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

/**
 * Secured controller only accessible by logged-in users.
 */
@RestController
@RequestMapping("/user/")
public class SecuredUserController {

    @NonNull
    UserAuthenticationService authentication;

    @Autowired
    private UserRepository userRepository;

    /**
     * GET endpoint retrieves user by id
     *
     * @param id (UUID) of the user
     * @return optional of user
     */
    @GetMapping("/getUser/{id}")
    public Optional<User> getUserById(@PathVariable(value = "id") UUID id) {
        return userRepository.findById(id);
    }

    /**
     * GET endpoint retrieves all existing users
     *
     * @return list of courses
     */
    @GetMapping("/getUsers")
    public List<User> getCourses() {
        return userRepository.findAll();
    }

    /**
     * GET endpoint to logout
     *
     * @return boolean
     */
    @GetMapping("/logout")
    boolean logout(@AuthenticationPrincipal final User user) {
        authentication.logout(user);
        return true;
    }
}
