package nl.tudelft.sem.User.controllers;

import lombok.NonNull;
import nl.tudelft.sem.DTO.LeaveRatingDTO;
import nl.tudelft.sem.User.entities.Role;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.UserRepository;
import nl.tudelft.sem.User.security.UserAuthenticationService;
import nl.tudelft.sem.User.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@Controller
public class SecuredUserController {

    @NonNull
    UserAuthenticationService authentication;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public Mono<Optional<User>> getUserById(UUID id) {
        Optional<User> user = userRepository.findById(id);
        return Mono.just(user);
    }

    public List<User> getUsers() {
        System.out.println(userRepository.findAll());
        return userRepository.findAll();
    }

    boolean logout(User user) {
        authentication.logout(user);
        return true;
    }

    public boolean createUser(User user) {
        userRepository.save(user);
        return true;
    }

    public boolean acceptApplication(UUID userId, UUID applicationId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("user not found"));
        if (user.getRole() != Role.LECTURER) {
            throw new Exception("invalid role: only lecturers can accept applications");
        }
        return userService.acceptTaApplication(applicationId);
    }

    public boolean createApplication(UUID userId, UUID courseId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("user not found"));
        if (user.getRole() != Role.STUDENT) {
            throw new Exception("invalid role: only students can create applications");
        }
        return userService.createApplication(userId, courseId);
    }

    public boolean deleteUser(UUID id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    /** POST endpoint for adding a rating to a TA.
     *
     * @param userId UUID of user making the request.
     * @param dto DTO provided by user making the request.
     * Throws 403 Forbidden when user is not lecturer for course.
     * Throws 400 Bad Request when there is no rating provided.
     * Throws 502 Bad Gateway when request to TA microservice fails.
     */
    @PostMapping("/addRating/{user_id}")
    void addRating(
        @PathVariable("user_id") UUID userId,
        @RequestBody LeaveRatingDTO dto)  {
        // Do validation
        if(userService.validateRole(userId, Role.LECTURER)) {
            if (dto.getRating().isPresent()) {
                // Send request to TA microservice
                try {
                    boolean result = userService.addRatingByTAId(dto.getId(), dto.getRating().get());
                    // !!! WHAT TO DO WITH RESULT?
                    // -> opperation unsuccesful
                } catch (Exception e) {
                    throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "External service did not deliver data");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No rating provided");
            }
        }
        else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to make request");
        }
    }

}

