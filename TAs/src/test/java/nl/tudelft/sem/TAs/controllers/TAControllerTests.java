package nl.tudelft.sem.TAs.controllers;

import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.TAs.entities.TA;
import nl.tudelft.sem.TAs.repositories.TARepository;
import org.junit.jupiter.api.Assertions;
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

@SpringBootTest
public class TAControllerTests {

    @Autowired
    TAController taController;

    @MockBean
    TARepository taRepository;

    @Test
    void noJobsYet_throwsException() {
        // Arrange
        UUID student = UUID.randomUUID();
        Mockito.when(taRepository.findAllByStudentId(student)).thenReturn(List.of());
        // Act
        Mono<RatingDTO> mono = taController.getAverageRating(student);
        // Assert
        RatingDTO result = mono.block();
        Assertions.assertNull(result);
    }

    @Test
    void testSingleJob_returnsCorrectValue() {
        // Arrange
        UUID student = UUID.randomUUID();
        TA ta1 = new TA(UUID.randomUUID(), student);
        ta1.setRating(4);
        List<TA> list = new ArrayList<>(List.of(ta1));
        Mockito.when(taRepository.findAllByStudentId(student)).thenReturn(list);
        // Act
        Mono<RatingDTO> mono = taController.getAverageRating(student);
        // Assert
        RatingDTO result = Objects.requireNonNull(mono.block());
        assertEquals(result.getStudentId(), student);
        assertTrue(result.getRating().isPresent());
        assertEquals(result.getRating().get(), 4);
    }

    @Test
    void testAverageOfThreeJobs_returnsAverageValue() {
        // Arrange
        UUID student = UUID.randomUUID();
        TA ta1 = new TA(UUID.randomUUID(), student);
        ta1.setRating(4);
        TA ta2 = new TA(UUID.randomUUID(), student);
        ta2.setRating(5);
        TA ta3 = new TA(UUID.randomUUID(), student);
        ta3.setRating(5);
        List<TA> list = new ArrayList<>(List.of(ta1, ta2, ta3));
        Mockito.when(taRepository.findAllByStudentId(student)).thenReturn(list);
        // Act
        Mono<RatingDTO> mono = taController.getAverageRating(student);
        // Assert
        RatingDTO result = Objects.requireNonNull(mono.block());
        assertEquals(result.getStudentId(), student);
        assertTrue(result.getRating().isPresent());
        assertEquals(result.getRating().get(), (5+5+4)/3);
    }

}
