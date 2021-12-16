package nl.tudelft.sem.TAs.controllers;

import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.TAs.entities.TA;
import nl.tudelft.sem.TAs.repositories.TARepository;
import nl.tudelft.sem.TAs.services.TAService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class TAControllerTests {

    @Autowired
    TAController taController;

    @MockBean
    TAService taService;

    @MockBean
    TARepository taRepository;

    UUID studentId;

    @BeforeEach
    void setup() {
        studentId = UUID.randomUUID();
    }

    @Test
    void getAverageRating_test() {
        // Act
        taController.getAverageRating(studentId);
        // Assert
        verify(taService).getAverageRating(studentId);
    }


}
