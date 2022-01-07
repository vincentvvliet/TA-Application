package nl.tudelft.sem.TAs.serviceTests;

import java.util.*;
import nl.tudelft.sem.DTO.LeaveRatingDTO;
import nl.tudelft.sem.DTO.RatingDTO;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    public void addRatingSuccessful() {
        TA ta = new TA(UUID.randomUUID(), UUID.randomUUID());
        LeaveRatingDTO dto = new LeaveRatingDTO(ta.getId(), Optional.of(5));
        when(taRepository.findById(ta.getId())).thenReturn(Optional.of(ta));
        taService.addRating(dto);
        verify(taRepository).save(any(ta.getClass()));
    }

    @Test
    public void addRatingNotSuccessful() {
        TA ta = new TA(UUID.randomUUID(), UUID.randomUUID());
        LeaveRatingDTO dto = new LeaveRatingDTO(ta.getId(), Optional.of(5));
        when(taRepository.findById(ta.getId())).thenReturn(Optional.empty());
        taService.addRating(dto);
        verify(taRepository, Mockito.never()).save(any(ta.getClass()));
    }
}
