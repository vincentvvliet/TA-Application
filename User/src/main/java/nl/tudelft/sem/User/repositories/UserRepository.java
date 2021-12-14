package nl.tudelft.sem.User.repositories;

import nl.tudelft.sem.User.entities.RealUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<RealUser, UUID> {
}
