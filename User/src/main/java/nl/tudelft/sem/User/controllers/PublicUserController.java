package nl.tudelft.sem.User.controllers;

import lombok.NonNull;
import nl.tudelft.sem.User.security.UserAuthenticationService;
import nl.tudelft.sem.User.repositories.UserRepository;
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
    @NonNull
    UserAuthenticationService authentication;

    @Autowired
    UserRepository users;

    /**
     * POST endpoint registers user by username and password
     *
     * @param username of the user
     * @param password of the user
     * @return optional of user
     */
    @PostMapping("/register")
    String register(@RequestParam("username") final String username,
                    @RequestParam("password") final String password) {
//        users.save(
//                User
//                        .builder()
//                        .username(username)
//                        .password(password)
//                        .build()
//        );

        return login(username, password);
    }

    /**
     * POST endpoint registers user by username and password
     *
     * @param username of the user
     * @param password of the user
     * @return optional of user
     */
    @PostMapping("/login")
    String login(@RequestParam("username") final String username,
                 @RequestParam("password") final String password) {
        System.out.println(username);
        System.out.println(password);
        System.out.println(authentication);
        return authentication
                .login(username, password)
                .orElseThrow(() -> new RuntimeException("invalid login and/or password"));
    }
}
