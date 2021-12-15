package nl.tudelft.sem.User.controllers;

import nl.tudelft.sem.User.entities.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Secured proxy for controller only accessible by logged-in users.
 */
@RestController
@RequestMapping("/user/")
public class ProxyController implements Controller {

    private SecuredUserController controller;

    /**
     * GET endpoint retrieves user by id.
     *
     * @param id (UUID) of the user
     * @return optional of user
     */
    @GetMapping("/getUser/{id}")
    @Override
    public Optional<User> getUserById(@PathVariable(value = "id") UUID id) {
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
    public List<User> getUsers() {
        check();
        return controller.getUsers();
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
     * PATCH endpoint to accept a TA application.
     *
     * @param userId        of the lecturer who accepts the application
     * @param applicationId of the application to be accepted
     * @return true if the application was successfully accepted, false otherwise
     */
    @RequestMapping("/acceptApplication/{userId}/{applicationId}")
    @Override
    public boolean acceptApplication(@PathVariable(value = "userId") UUID userId, @PathVariable(value = "applicationId") UUID applicationId) throws Exception {
        check();
        return controller.acceptApplication(userId, applicationId);
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
    public boolean createApplication(@PathVariable(value = "userId") UUID userId, @PathVariable(value = "courseId") UUID courseId) throws Exception {
        check();
        return controller.createApplication(userId, courseId);
    }

    /**
     * Check whether a controller already exists, if not then create new controller.
     */
    private void check() {
        if (controller == null) {
            controller = new SecuredUserController();
        }
    }
}
