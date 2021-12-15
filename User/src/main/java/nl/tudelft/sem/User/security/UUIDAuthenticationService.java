package nl.tudelft.sem.User.security;

import lombok.NonNull;
import nl.tudelft.sem.User.entities.Role;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UUIDAuthenticationService implements UserAuthenticationService {
    @NonNull
    @Autowired
    UserRepository userRepository;

    @Override
    public Optional<UUID> login(String username, String password, String role) {
        User user = new User(username, password, Role.valueOf(role));

        userRepository.save(user);
        return Optional.of(user.getId());
    }

    @Override
    public Optional<User> findByToken(final String token) {
        return userRepository.findById(UUID.fromString(token));
    }

    @Override
    public void logout(final User user) {

    }
}
