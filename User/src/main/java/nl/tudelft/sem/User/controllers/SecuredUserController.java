package nl.tudelft.sem.User.controllers;

import lombok.NonNull;
import nl.tudelft.sem.User.entities.Role;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.UserRepository;
import nl.tudelft.sem.User.security.TokenAuthenticationService;
import nl.tudelft.sem.User.security.UserAuthenticationService;
import nl.tudelft.sem.User.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
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
    TokenAuthenticationService authentication;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

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

    /**
     * PATCH endpoint to accept a TA application.
     *
     * @param userId        of the lecturer who accepts the application
     * @param applicationId of the application to be accepted
     * @return true if the application was successfully accepted, false otherwise
     */
    @RequestMapping("/acceptApplication/{userId}/{applicationId}")
    public Mono<Boolean> acceptApplication(@PathVariable(value = "userId") UUID userId, @PathVariable(value = "applicationId") UUID applicationId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("user not found"));
        if (user.getRole() != Role.LECTURER) {
            throw new Exception("invalid role: only lecturers can accept applications");
        }
        return Mono.just(userService.acceptTaApplication(applicationId));
    }

    /**
     * PATCH endpoint to accept a TA application.
     *
     * @param userId   of the user the application is of
     * @param courseId the course the application is for
     * @return true if the application was successfully created, false otherwise
     */
    @PostMapping("/createApplication/{userId}/{courseId}")
    public Mono<Boolean> createApplication(@PathVariable(value = "userId") UUID userId, @PathVariable(value = "courseId") UUID courseId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("user not found"));
        if (user.getRole() != Role.STUDENT) {
            throw new Exception("invalid role: only students can create applications");
        }
        return Mono.just(userService.createApplication(userId, courseId));
    }
}
