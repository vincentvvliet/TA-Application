package nl.tudelft.sem.User.controllers;

import nl.tudelft.sem.DTO.ApplicationDTO;
import nl.tudelft.sem.DTO.ApplyingStudentDTO;
import nl.tudelft.sem.User.entities.Role;
import nl.tudelft.sem.User.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


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
     * @param id    (UUID) of the user
     * @param token JWT token
     * @return optional of user
     */
    @GetMapping("/getUser/{id}/{token}")
    @Override
    public Mono<Optional<User>> getUserById(@PathVariable(value = "id") UUID id, @PathVariable(value = "token") String token) {
        controller.validateRole(id, null);
        return controller.getUserById(id);
    }

    /**
     * GET endpoint retrieves all existing users.
     *
     * @param token JWT token
     * @return list of courses
     */
    @GetMapping("/getUsers/{token}")
    @Override
    public Flux<User> getUsers(@PathVariable(value = "token") String token) {
        validateToken("token");
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
        validateToken("token");
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
        //TODO return JWT token
        return Mono.just(controller.createUser(new User(username, password, Role.valueOf(role))));
    }


    /**
     * PATCH endpoint to accept a TA application.
     *
     * @param userId        of the lecturer who accepts the application
     * @param applicationId of the application to be accepted
     * @param token         JWT token
     * @return true if the application was successfully accepted, false otherwise
     */
    @RequestMapping("/acceptApplication/{userId}/{applicationId}/{token}")
    @Override
    public Mono<Boolean> acceptApplication(@PathVariable(value = "userId") UUID userId, @PathVariable(value = "applicationId") UUID applicationId, @PathVariable(value = "token") String token) throws Exception {
        validateToken(token);
        controller.validateRole(userId, Role.valueOf("LECTURER"));
        return Mono.just(controller.acceptApplication(userId, applicationId));
    }

    /**
     * PATCH endpoint to accept a TA application.
     *
     * @param userId   of the user the application is of
     * @param courseId the course the application is for
     * @param token    JWT token
     * @return true if the application was successfully created, false otherwise
     */
    @PostMapping("/createApplication/{userId}/{courseId}/{token}")
    @Override
    public Mono<Boolean> createApplication(@PathVariable(value = "userId") UUID userId, @PathVariable(value = "courseId") UUID courseId, @PathVariable(value = "token") String token) throws Exception {
        validateToken(token);
        controller.validateRole(userId, Role.valueOf("STUDENT"));
        return Mono.just(controller.createApplication(userId, courseId));
    }

    /**
     * GET endpoint to accept a TA application.
     *
     * @param token JWT token
     * @return true if the application was successfully created, false otherwise
     */
    @GetMapping("/getAllApplications/{token}")
    public Flux<ApplicationDTO> getAllApplications(@PathVariable(value = "token") String token) {
        validateToken(token);
        return Flux.fromIterable(controller.getAllApplications());
    }

    /**
     * Gets a single application.
     *
     * @param userId   the user id
     * @param courseId the course id
     * @param token    the token
     * @return the application
     */
    @GetMapping("/getApplication/{userId}/{courseId}/{token}")
    public Mono<ApplicationDTO> getApplication(@PathVariable(value = "userId") UUID userId, @PathVariable(value = "courseId") UUID courseId, @PathVariable(value = "token") String token) {
        validateToken(token);
        return Mono.just(controller.getApplication(userId, courseId));
    }

    /**
     * Gets an overview of all applications.
     *
     * @param courseId the course id
     * @param token    the token
     * @return the applications overview
     */
    @GetMapping("/getApplicationOverview/{courseId}/{token}")
    public Flux<ApplyingStudentDTO> getApplicationsOverview(@PathVariable(value = "courseId") UUID courseId, @PathVariable(value = "token") String token) {
        validateToken(token);
        return Flux.fromIterable(controller.getApplicationsOverview(courseId));
    }

    /**
     * DELETE endpoint deletes a user by id
     *
     * @param id    of the user
     * @param token JWT token
     * @return boolean representing if the deletion was successful or not
     */
    @DeleteMapping("deleteUser/{id}/{token}")
    public Mono<Boolean> deleteUser(@PathVariable(value = "id") UUID id, @PathVariable(value = "token") String token) {
        validateToken(token);
        controller.validateRole(id, null);
        return Mono.just(controller.deleteUser(id));
    }


    /**
     * Validate a token.
     */
    private void validateToken(String token) {
        //TODO validate JWT token -> change String token to actual token and check
        if (token.equals("")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Incorrect token provided");
        }
    }
}
