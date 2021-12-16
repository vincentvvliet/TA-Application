package nl.tudelft.sem.User.controllers;

import lombok.NonNull;
import nl.tudelft.sem.User.entities.Role;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.UserRepository;
import nl.tudelft.sem.User.security.UserAuthenticationService;
import nl.tudelft.sem.User.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
public class SecuredUserController {

    @NonNull
    UserAuthenticationService authentication;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public Mono getUserById(UUID id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return Mono.empty();
        }
        return Mono.just(user.get());
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
}
