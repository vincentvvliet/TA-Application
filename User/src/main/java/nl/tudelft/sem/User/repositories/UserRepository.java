package nl.tudelft.sem.User.repositories;

import nl.tudelft.sem.User.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find by username.
     *
     * @param username the username
     * @return Optional of User if found in database
     */
    default Optional<User> findByUsername(String username) {
        return this.findAll()
                .stream()
                .filter(u -> Objects.equals(username, u.getUsername()))
                .findFirst();
    }
}
