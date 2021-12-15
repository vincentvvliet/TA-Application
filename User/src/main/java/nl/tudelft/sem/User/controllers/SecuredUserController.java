package nl.tudelft.sem.User.controllers;

import lombok.NonNull;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.entities.Role;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.UserRepository;
import nl.tudelft.sem.User.security.UserAuthenticationService;
import nl.tudelft.sem.User.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.List;


public class SecuredUserController {

    @NonNull
    UserAuthenticationService authentication;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }


    boolean logout(User user) {
        authentication.logout(user);
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

}
