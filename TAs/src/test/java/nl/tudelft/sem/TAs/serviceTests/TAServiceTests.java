package nl.tudelft.sem.TAs.serviceTests;

import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.TAs.controllers.TAController;
import nl.tudelft.sem.TAs.entities.TA;
import nl.tudelft.sem.TAs.repositories.TARepository;
import nl.tudelft.sem.TAs.services.TAService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TAServiceTests {

    @Autowired
    TAService taService;

    @MockBean
    TARepository taRepository;

    @Test
    void noJobsYet_isNull() {
        // Arrange
        UUID student = UUID.randomUUID();
        Mockito.when(taRepository.getAverageRating(student)).thenReturn(Optional.empty());
        // Act
        Mono<RatingDTO> mono = taService.getAverageRating(student);
        // Assert
        RatingDTO result = mono.block();
        Assertions.assertNull(result);
    }

    @Test
    void testSingleJob_returnsCorrectValue() {
        // Arrange
        UUID student = UUID.randomUUID();
        int ratingTA = 4;
        Mockito.when(taRepository.getAverageRating(student)).thenReturn(Optional.of(ratingTA));
        // Act
        Mono<RatingDTO> mono = taService.getAverageRating(student);
        // Assert
        RatingDTO result = Objects.requireNonNull(mono.block());
        assertEquals(result.getStudentId(), student);
        assertEquals(result.getRating(), ratingTA);
    }

    @Test
    void testAverageOfThreeJobs_returnsAverageValue() {
        // Arrange
        UUID student = UUID.randomUUID();
        int averageRatingTA = (4 + 5 + 5) / 3;
        Mockito.when(taRepository.getAverageRating(student)).thenReturn(Optional.of(averageRatingTA));
        // Act
        Mono<RatingDTO> mono = taService.getAverageRating(student);
        // Assert
        RatingDTO result = Objects.requireNonNull(mono.block());
        assertEquals(result.getStudentId(), student);
        assertEquals(result.getRating(), averageRatingTA);
    }
}
