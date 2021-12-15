package nl.tudelft.sem.User.controllers;

import lombok.NonNull;
import nl.tudelft.sem.User.entities.Role;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.security.UserAuthenticationService;
import nl.tudelft.sem.User.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


/**
 * Secured controller accessible by all users
 */
@RestController
@RequestMapping("/user/")
public class PublicUserController {
    @NonNull
    UserAuthenticationService authentication;

    @Autowired
    UserRepository userRepository;

    /**
     * POST endpoint registers user by username and password
     *
     * @param username of the user
     * @param password of the user
     * @return optional of user
     */
    @PostMapping("/register")
    UUID register(@RequestParam("username") String username,
                    @RequestParam("password") String password,
                    @RequestParam("password") String role) {
        userRepository.save(new User(username, password, Role.valueOf(role)));

        return login(username, password, role);
    }

    /**
     * POST endpoint registers user by username and password
     *
     * @param username of the user
     * @param password of the user
     * @return optional of user
     */
    @PostMapping("/login")
    UUID login(@RequestParam("username") String username,
                 @RequestParam("password") String password,
                 @RequestParam("password") String role) {
        System.out.println(username);
        System.out.println(password);
        System.out.println(authentication);
        return authentication
                .login(username, password, role)
                .orElseThrow(() -> new RuntimeException("invalid login and/or password"));
    }
}
