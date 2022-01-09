package nl.tudelft.sem.User.controllers;

import lombok.NonNull;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.UserRepository;
import nl.tudelft.sem.User.security.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

/**
 * Secured controller only accessible by logged-in users.
 */
@RestController
@RequestMapping("/user/")
public class SecuredUserController {

    @NonNull
    TokenAuthenticationService authentication;

    @Autowired
    private UserRepository userRepository;

    /**
     * GET endpoint retrieves user by id.
     *
     * @param id (UUID) of the user
     * @return optional of user
     */
    @GetMapping("/getUser/{id}")
    public Mono<Optional<User>> getUserById(@PathVariable(value = "id") UUID id) {
        return Mono.just(userRepository.findById(id));
    }

    /**
     * GET endpoint retrieves all existing users.
     *
     * @return list of courses
     */
    @GetMapping("/getUsers")
    public Flux<User> getUsers() {
        return Flux.fromIterable(userRepository.findAll());
    }

    /**
     * GET endpoint to logout.
     *
     * @return boolean
     */
    @GetMapping("/logout")
    boolean logout(@AuthenticationPrincipal final User user) {
        System.out.println(user); //TODO user null
        authentication.logout(user);
        return true;
    }
}
