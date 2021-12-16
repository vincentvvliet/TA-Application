package nl.tudelft.sem.User.controllers;

import nl.tudelft.sem.User.entities.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

public interface Controller {
    public Mono<Optional<User>> getUserById(UUID id);

    public Flux<User> getUsers();

    boolean logout(User user);

    public Mono<Boolean> acceptApplication(UUID userId, UUID applicationId) throws Exception;

    public Mono<Boolean> createApplication(UUID userId, UUID courseId) throws Exception;

    public Mono<Boolean> deleteUser(UUID id);
}