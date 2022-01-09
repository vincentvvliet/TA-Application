package nl.tudelft.sem.User.serviceTests;
import nl.tudelft.sem.User.entities.Notification;
import nl.tudelft.sem.User.entities.Role;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.NotificationRepository;
import nl.tudelft.sem.User.repositories.UserRepository;
import nl.tudelft.sem.User.services.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static nl.tudelft.sem.User.entities.Role.ADMIN;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class UserServiceTests {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    NotificationRepository notificationRepository;

    @Test
    public void getAndRemoveNotificationsByUserNameTest() {
        String username = "username";
        String m = "message";
        User user = new User(username,"password", ADMIN );
        Notification n = new Notification(user.getId(), m);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(notificationRepository.findNotificationsByRecipientId(user.getId())).thenReturn(Collections.singletonList(n));

        String ret = userService.getAndRemoveNotificationsByUserName(username);
        verify(notificationRepository).removeByRecipientId(user.getId());
        Assertions.assertThat(ret.contains(m));
    }

    @Test
    public void getAndRemoveNotificationsByUserName_NoUser() {
        String username = "username";
        String m = "none";
        User user = new User(username, "password", ADMIN );
        Notification n = new Notification(user.getId(), m);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        String ret = userService.getAndRemoveNotificationsByUserName(username);
        verify(notificationRepository, never()).removeByRecipientId(user.getId());
        verify(notificationRepository, never()).findNotificationsByRecipientId(user.getId());
        Assertions.assertThat(ret.contains(m));
    }


}
