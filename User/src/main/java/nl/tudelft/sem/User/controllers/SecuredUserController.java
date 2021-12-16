package nl.tudelft.sem.User.controllers;

import lombok.NonNull;
import nl.tudelft.sem.DTO.ApplicationDTO;
import nl.tudelft.sem.DTO.ApplyingStudentDTO;
import nl.tudelft.sem.User.entities.Role;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.UserRepository;
import nl.tudelft.sem.User.security.UserAuthenticationService;
import nl.tudelft.sem.User.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

/**
 * The type Secured user controller.
 */
@RestController
public class SecuredUserController {

    /**
     * The Authentication.
     */
    @NonNull
    UserAuthenticationService authentication;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    /**
     * Gets user by id.
     *
     * @param id the id
     * @return the user by id
     */
    public Mono<Optional<User>> getUserById(UUID id) {
        Optional<User> user = userRepository.findById(id);
        return Mono.just(user);
    }

    /**
     * Gets all users.
     *
     * @return list of users
     */
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Logout user.
     *
     * @param user the user
     * @return true if logout was successful
     */
    boolean logout(User user) {
        authentication.logout(user);
        return true;
    }

    /**
     * Create user.
     *
     * @param user the user
     * @return true if creation was successful
     */
    public boolean createUser(User user) {
        userRepository.save(user);
        return true;
    }

    /**
     * Accept application.
     *
     * @param userId        the user id
     * @param applicationId the application id
     * @return true if accepting application was successful
     * @throws Exception if user not found in database
     */
    public boolean acceptApplication(UUID userId, UUID applicationId) throws Exception {
//        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("user not found"));
//        if (user.getRole() != Role.LECTURER) {
//            throw new Exception("invalid role: only lecturers can accept applications");
//        }

        return userService.acceptTaApplication(applicationId);
    }

    /**
     * Create application.
     *
     * @param userId   the user id
     * @param courseId the course id
     * @return true if creation was successful
     * @throws Exception if user not found in database
     */
    public boolean createApplication(UUID userId, UUID courseId) throws Exception {
//        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("user not found"));
//        if (user.getRole() != Role.STUDENT) {
//            throw new Exception("invalid role: only students can create applications");
//        }

        return userService.createApplication(userId, courseId);
    }

    /**
     * Gets all applications.
     *
     * @return all applications
     */
    public List<ApplicationDTO> getAllApplications() {
        return userService.getAllApplications();
    }

    /**
     * Gets all applications.
     *
     * @return all applications
     */
    public ApplicationDTO getApplication(UUID studentId, UUID courseId) {
        return userService.getApplication(studentId, courseId);
    }

    public List<ApplyingStudentDTO> getApplicationsOverview(UUID courseId) {
        return userService.getApplicationsOverview(courseId);
    }

    /**
     * Delete user boolean.
     *
     * @param id the id
     * @return the boolean
     */
    public boolean deleteUser(UUID id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate.
     *
     * @param id   the id
     * @param role the role
     */
    public void validateRole(UUID id, Role role) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("user not found"));
        // If role == null, then all roles have access
        if (user.getRole() != role && role != null) {
            // User does not have correct role to gain access
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to make request. Only " + role + "'s are authorized to do this.");
        }
    }
}
