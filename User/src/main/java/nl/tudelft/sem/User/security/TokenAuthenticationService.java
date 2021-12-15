package nl.tudelft.sem.User.security;

import lombok.NonNull;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenAuthenticationService implements UserAuthenticationService {
    @NonNull
    TokenService tokens;
    @Autowired
    UserRepository userRepository;

    @Override
    public Optional<UUID> login(String username, String password, String role) {
        return Optional.of(UUID.fromString(userRepository.findAll()
                .stream()
                .filter(u -> Objects.equals(username, u.getUsername()))
                .findFirst()
                .filter(user -> Objects.equals(password, user.getPassword()))
                .map(user -> tokens.expiring(Map.of("username", username))).get()));
    }

    @Override
    public Optional<User> findByToken(String token) {
        Optional<Object> temp = Optional.of(tokens.verify(token))
                .map(map -> map.get("username"));
        return userRepository
                .findAll()
                .stream()
                .filter(u -> Objects.equals(temp, u.getUsername()))
                .findFirst();
    }

    @Override
    public void logout(final User user) {
        // Nothing
    }
}