package nl.tudelft.sem.User.controllers;

import java.util.List;
import java.util.UUID;
import nl.tudelft.sem.User.entities.Notification;
import nl.tudelft.sem.User.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/notification/")
public class NotificationController {

    @Autowired
    NotificationRepository notificationRepository;


    /** Post endpoint creates new notification.
     *
     * @param recipientId id of recipient of notification.
     * @param message message for recipient.
     */

    @PostMapping("/createNotification/{recipientId}/{message}")
    public void createNotification(
            @PathVariable(value = "recipientId") UUID recipientId,
            @PathVariable(value = "message") String message) {
        Notification notification = new Notification(recipientId, message);
        notificationRepository.save(notification);
    }

    /**
     * Get endpoint for all notifications.
     *
     * @return flux of notifications.
     */
    @GetMapping("/getAllNotifications/")
    public Flux<Notification> getAllNotifications() {
        return Flux.fromIterable(notificationRepository.findAll());
    }

    /**
     * Get endpoint for notifications for specified user.
     *
     * @param recipientId id of user.
     * @return flux of notifications.
     */
    @GetMapping ("/getNotificationsForUser/{recipientId}")
    public Flux<Notification> getNotificationsForUser(
            @PathVariable(value = "recipientId") UUID recipientId) {
        List<Notification> notifications =
                notificationRepository.findNotificationsByRecipientId(recipientId);
        return Flux.fromIterable(notifications);
    }

}
