package nl.tudelft.sem.User.controllers;

import nl.tudelft.sem.User.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Controller {
    public Optional<User> getUserById(UUID id);

    public List<User> getUsers();

    boolean logout(User user);

    public boolean acceptApplication(UUID userId, UUID applicationId) throws Exception;

    public boolean createApplication(UUID userId, UUID courseId) throws Exception;
}
