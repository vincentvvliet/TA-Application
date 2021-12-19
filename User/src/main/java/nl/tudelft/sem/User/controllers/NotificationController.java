package nl.tudelft.sem.User.controllers;

import java.util.UUID;
import nl.tudelft.sem.User.entities.Notification;
import nl.tudelft.sem.User.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
