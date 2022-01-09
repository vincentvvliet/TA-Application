package nl.tudelft.sem.User.controllers;

import java.util.*;
import nl.tudelft.sem.User.entities.Notification;
import nl.tudelft.sem.User.repositories.NotificationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class NotificationControllerTest {

    @Autowired
    NotificationController notificationController;

    @MockBean
    NotificationRepository notificationRepository;

    UUID recipientId;
    String message;
    Notification notification;


    @BeforeEach
    public void setup() {
        recipientId = UUID.fromString("3628a115-6d1a-4ae0-a978-32b69b8200b8");
        message = "test Notification";
        notification = new Notification(recipientId, message);
    }

    @Test
    public void createNotificationTest() {
        notificationController.createNotification(recipientId, message);
        verify(notificationRepository).save(any(Notification.class));

    }

    @Test
    public void getByUserTest() {
        when(notificationRepository.findNotificationsByRecipientId(recipientId))
                .thenReturn(Collections.singletonList(notification));
        Flux<Notification> result = notificationController.getNotificationsForUser(recipientId);
        Assertions.assertEquals(result.blockFirst().getRecipientId(), recipientId);
    }

    @Test
    public void getAllTest() {
        when(notificationRepository.findAll())
                .thenReturn(Collections.singletonList(notification));
        Flux<Notification> result = notificationController.getAllNotifications();
        Assertions.assertEquals(result.blockFirst().getRecipientId(), recipientId);
    }
}

