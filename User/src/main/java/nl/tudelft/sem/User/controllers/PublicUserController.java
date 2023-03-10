package nl.tudelft.sem.User.controllers;

import lombok.NonNull;
import nl.tudelft.sem.User.entities.Role;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.UserRepository;
import nl.tudelft.sem.User.security.TokenAuthenticationService;
import nl.tudelft.sem.User.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Secured controller accessible by all users
 */
@RestController
@RequestMapping("/user/")
public class PublicUserController {
    @Autowired
    @NonNull
    TokenAuthenticationService authentication;

    @Autowired
    UserRepository userRepository;

    UserService userService;

    /**
     * POST endpoint registers user by username and password
     *
     * @param username of the user
     * @param password of the user
     * @return optional of user
     */
    @PostMapping("/register")
    String register(@RequestParam("username") String username,
                    @RequestParam("password") String password,
                    @RequestParam("role") String role) {
        userRepository.save(new User(username, password, Role.valueOf(role)));

        return login(username, password, role);
    }

    /**
     * POST endpoint for a user to log in. Returns notifications for user o succesful login.
     *
     * @param username of the user
     * @param password of the user
     * @return Notifications for user.
     */
    @PostMapping("/login")
    String login(@RequestParam("username") String username,
                 @RequestParam("password") String password,
                 @RequestParam(value = "role", required = false) String role) {
        // Logging in without specifying role
        if (role == null) {
            // Get role from database
            role = String.valueOf(userRepository.findByUsername(username).get().getRole());
        }
        System.out.println(role);

        authentication
                .login(username, password, role)
                .orElseThrow(() -> new RuntimeException("invalid login and/or password"));
        return userService.getAndRemoveNotificationsByUserName(username);
    }
}
