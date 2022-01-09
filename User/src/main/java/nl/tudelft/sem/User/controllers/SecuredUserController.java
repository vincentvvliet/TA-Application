package nl.tudelft.sem.User.controllers;

import lombok.NonNull;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.UserRepository;
import nl.tudelft.sem.User.security.UserAuthenticationService;
import nl.tudelft.sem.User.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
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

    public boolean deleteUser(UUID id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}

