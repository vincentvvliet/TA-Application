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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest(ProxyController.class)
public class ProxyControllerTest {

    @MockBean
    ProxyController proxyController;

    @MockBean
    SecuredUserController securedUserController;

    @MockBean
    UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    User user;
    List<User> userList;
    UUID id;

    @BeforeEach
    public void setup() {
        user = new User();
        userList = new ArrayList<>();
        id = UUID.randomUUID();
        userList.add(user);
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(user));
    }

//    @Test
//    public void correctTokenTest() throws Exception {
//        when(securedUserController.getUsers()).thenReturn(userList);
//        this.mockMvc.perform(MockMvcRequestBuilders.get("/user/getUsers"))
//                .andExpect(MockMvcResultMatchers.status().is(404));
//    }
//
//    @Test
//    public void incorrectTokenTest() throws Exception {
//        when(securedUserController.getUsers()).thenReturn(userList);
//        this.mockMvc.perform(MockMvcRequestBuilders.get("/user/getUsers"))
//                .andExpect(MockMvcResultMatchers.status().is(404));
//    }
}