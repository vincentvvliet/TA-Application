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
    UUID courseId;
    TA ta;

    @BeforeEach
    void setup() {
        studentId = UUID.randomUUID();
        courseId = UUID.randomUUID();
        ta = new TA(courseId, studentId);
    }

    @Test
    void getAverageRating_test() {
        // Act
        taController.getAverageRating(studentId);
        // Assert
        verify(taService).getAverageRating(studentId);
    }

    @Test
    public void addTimeSpend_successful() {
        int timeSpent = 3;
        Mockito.when(taRepository.findById(ta.getId())).thenReturn(Optional.of(ta));
        Mockito.when(taService.isCourseFinished(courseId, 47112)).thenReturn(true);

        Assertions.assertEquals(true, taController.addTimeSpent(ta.getId(), timeSpent).block());
        Assertions.assertEquals(timeSpent, ta.getTimeSpent());
        verify(taRepository).save(any(TA.class));
    }

    @Test
    public void addTimeSpent_invalidId() {
        Mockito.when(taRepository.findById(ta.getId())).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(Exception.class, () -> taController.addTimeSpent(ta.getId(), 3).block());
        String expectedMessage = "no TA was found with the given id";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(taRepository, never()).save(any(TA.class));
    }

    @Test
    public void addTimeSpent_negativeHours(){
        Mockito.when(taRepository.findById(ta.getId())).thenReturn(Optional.of(ta));

        Exception exception = Assertions.assertThrows(Exception.class, () -> taController.addTimeSpent(ta.getId(), -3).block());
        String expectedMessage = "number of hours spent must be positive";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(taRepository, never()).save(any(TA.class));
    }

    @Test
    public void addTimeSpent_courseNotFinished() {
        Mockito.when(taRepository.findById(ta.getId())).thenReturn(Optional.of(ta));
        Mockito.when(taService.isCourseFinished(courseId, 47112)).thenReturn(false);

        Assertions.assertEquals(false, taController.addTimeSpent(ta.getId(), 3).block());
        Assertions.assertNotEquals(3, ta.getTimeSpent());
        verify(taRepository, never()).save(any(TA.class));
    }


}
