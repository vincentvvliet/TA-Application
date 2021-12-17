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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

/**
 * The type Secured user controller.
 */
@RestController
public class SecuredUserController {

    @NonNull
    UserAuthenticationService authentication;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    /**
     * Accept application.
     *
     * @param applicationId the application id
     * @return true if accepting application was successful
     * @throws Exception if user not found in database
     */
    public boolean acceptApplication(UUID applicationId) {
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
    public boolean createApplication(UUID userId, UUID courseId) {
        return userService.createApplication(userId, courseId);
    }

    /**
     * Get all applications.
     *
     * @return all applications
     */
    public List<ApplicationDTO> getAllApplications() {
        return userService.getAllApplications();
    }

    /**
     * Get all applications.
     *
     * @param studentId the student id
     * @param courseId  the course id
     * @return all applications
     */
    public ApplicationDTO getApplication(UUID studentId, UUID courseId) {
        return userService.getApplication(studentId, courseId);
    }

    /**
     * Gets applications overview.
     *
     * @param courseId the course id
     * @return the applications overview
     */
    public List<ApplyingStudentDTO> getApplicationsOverview(UUID courseId) {
        return userService.getApplicationsOverview(courseId);
    }

    /**
     * Delete user.
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
     * Validate role.
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
