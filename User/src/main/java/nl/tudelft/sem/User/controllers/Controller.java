package nl.tudelft.sem.User.controllers;

import nl.tudelft.sem.User.entities.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public interface Controller {
    public Mono<Optional<User>> getUserById(UUID id, String token);

    public Flux<User> getUsers(String token);

    boolean logout(User user);

    public Mono<Boolean> acceptApplication(UUID userId, UUID applicationId, String token) throws Exception;

    public Mono<Boolean> createApplication(UUID userId, UUID courseId, String token) throws Exception;

    public Mono<Boolean> deleteUser(UUID id, UUID ownId, String token);
}
