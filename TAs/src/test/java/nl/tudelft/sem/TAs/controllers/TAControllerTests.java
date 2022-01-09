package nl.tudelft.sem.TAs.controllers;

import java.util.*;
import nl.tudelft.sem.DTO.LeaveRatingDTO;
import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.TAs.entities.TA;
import nl.tudelft.sem.TAs.repositories.TARepository;
import nl.tudelft.sem.TAs.services.TAService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
