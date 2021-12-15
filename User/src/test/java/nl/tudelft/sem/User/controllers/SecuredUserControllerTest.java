package nl.tudelft.sem.User.controllers;

import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.UserRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class SecuredUserControllerTest {

    @Autowired
    SecuredUserController controller;

    @MockBean
    UserRepository userRepository;

    User user = new User();
    List<User> userList = new ArrayList<>();
    UUID id = UUID.randomUUID();

    @BeforeEach
    public void setup() {
        userList.add(user);
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(user));
    }

    @Test
    public void findByIdTest() {
        Assertions.assertEquals(controller.getUserById(id), Optional.ofNullable(user));
    }

    @Test
    public void findAllTest() {
        when(userRepository.findAll()).thenReturn(userList);
        Assertions.assertEquals(controller.getUsers(), userList);
    }

    @Test
    public void createTest() {
        controller.createUser(user);
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void deleteTest() {
        controller.deleteUser(id);
        verify(userRepository).deleteById(id);
    }
}
