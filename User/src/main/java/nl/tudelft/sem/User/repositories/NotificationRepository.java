package nl.tudelft.sem.User.repositories;

import java.util.UUID;
import nl.tudelft.sem.User.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}
