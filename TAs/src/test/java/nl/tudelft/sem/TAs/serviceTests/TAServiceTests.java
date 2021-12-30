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
        Mockito.when(taRepository.findAllByStudentId(student)).thenReturn(List.of());
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
        TA ta1 = new TA(UUID.randomUUID(), student);
        ta1.setRating(4);
        List<TA> list = new ArrayList<>(List.of(ta1));
        Mockito.when(taRepository.findAllByStudentId(student)).thenReturn(list);
        // Act
        Mono<RatingDTO> mono = taService.getAverageRating(student);
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
        Mono<RatingDTO> mono = taService.getAverageRating(student);
        // Assert
        RatingDTO result = Objects.requireNonNull(mono.block());
        assertEquals(result.getStudentId(), student);
        assertTrue(result.getRating().isPresent());
        assertEquals(result.getRating().get(), (5 + 5 + 4) / 3);
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
