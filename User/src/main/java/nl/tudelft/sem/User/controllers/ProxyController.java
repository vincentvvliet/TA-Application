package nl.tudelft.sem.User.controllers;

import nl.tudelft.sem.User.entities.Role;
import nl.tudelft.sem.User.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Secured proxy for controller only accessible by logged-in users.
 */
@RestController
@RequestMapping("/user/")
public class ProxyController implements Controller {

    @Autowired
    private SecuredUserController controller;

    /**
     * GET endpoint retrieves user by id.
     *
     * @param id (UUID) of the user
     * @return optional of user
     */
    @GetMapping("/getUser/{id}")
    @Override
    public Mono<Optional<User>> getUserById(@PathVariable(value = "id") UUID id) {
        check();
        return controller.getUserById(id);
    }

    /**
     * GET endpoint retrieves all existing users.
     *
     * @return list of courses
     */
    @GetMapping("/getUsers")
    @Override
    public Flux<User> getUsers() {
        check();
        return Flux.fromIterable(controller.getUsers());
    }

    /**
     * GET endpoint to logout.
     *
     * @return boolean
     */
    @GetMapping("/logout")
    @Override
    public boolean logout(@AuthenticationPrincipal User user) {
        check();
        return controller.logout(user);
    }

    /**
     * POST endpoint for creating a user.
     *
     * @param username of the user
     * @param password of the user
     * @param role     of the user
     * @return true if the user is properly created and saved in the database
     */
    @PostMapping("/createUser")
    public Mono<Boolean> createUser(@RequestParam String username, @RequestParam String password, @RequestParam String role) {
        check();
        return Mono.just(controller.createUser(new User(username, password, Role.valueOf(role))));
    }


    /**
     * PATCH endpoint to accept a TA application.
     *
     * @param userId        of the lecturer who accepts the application
     * @param applicationId of the application to be accepted
     * @return true if the application was successfully accepted, false otherwise
     */
    @RequestMapping("/acceptApplication/{userId}/{applicationId}")
    @Override
    public Mono<Boolean> acceptApplication(@PathVariable(value = "userId") UUID userId, @PathVariable(value = "applicationId") UUID applicationId) throws Exception {
        check();
        return Mono.just(controller.acceptApplication(userId, applicationId));
    }

    /**
     * PATCH endpoint to accept a TA application.
     *
     * @param userId   of the user the application is of
     * @param courseId the course the application is for
     * @return true if the application was successfully created, false otherwise
     */
    @PostMapping("/createApplication/{userId}/{courseId}")
    @Override
    public Mono<Boolean> createApplication(@PathVariable(value = "userId") UUID userId, @PathVariable(value = "courseId") UUID courseId) throws Exception {
        check();
        return Mono.just(controller.createApplication(userId, courseId));
    }

    /**
     * DELETE endpoint deletes a user by id
     *
     * @param id of the user
     * @return boolean representing if the deletion was successful or not
     */
    @DeleteMapping("deleteUser/{id}")
    public Mono<Boolean> deleteUser(@PathVariable(value = "id") UUID id) {
        check();
        return Mono.just(controller.deleteUser(id));
    }


    /**
     * Check whether a controller already exists, if not then create new controller.
     */
    private void check() {
        //TODO change to checking whether actual token is valid
        String token = "token";
        if (token.equals("token")) {
//            controller = new SecuredUserController();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Token: " + token + " is not correct.");
        }
    }
}
