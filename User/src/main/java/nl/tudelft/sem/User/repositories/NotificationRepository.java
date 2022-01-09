package nl.tudelft.sem.User.repositories;

import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.User.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findNotificationsByRecipientId(UUID recipientId);
    void removeByRecipientId(UUID revipientID);
}
