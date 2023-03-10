package nl.tudelft.sem.User.services;

import nl.tudelft.sem.User.entities.Notification;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.NotificationRepository;
import nl.tudelft.sem.User.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Finds all notifications for a given user, formats them to be displayed,
     * and removes them from the database so they are not shown twice.
     *
     * @param username of the user logging in.
     * @return notifications for the user.
     */
    public String getAndRemoveNotificationsByUserName(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        String ret = "Login successful. You have the following notifications: ";
        if (user.isPresent()) {
            UUID userId = user.get().getId();
            List<Notification> notifications =
                    notificationRepository.findNotificationsByRecipientId(userId);
            for (Notification n : notifications) {
                ret = ret.concat(n.getMessage() + ",\n");
            }
            notificationRepository.removeByRecipientId(userId);
        } else {
            return ret + "none";
        }
        return ret;
    }
}
