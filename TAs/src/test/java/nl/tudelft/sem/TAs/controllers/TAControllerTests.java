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

@SpringBootTest
public class TAControllerTests {

    @Autowired
    TAController taController;

    @MockBean
    TAService taService;

    @MockBean
    TARepository taRepository;

    UUID studentId = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();
    LeaveRatingDTO leaveRatingDTO;
    TA ta = new TA(courseId, studentId);
    List<TA> taList = new ArrayList<>();

    @BeforeEach
    void setup() {
        ta.setRating(3);
        taList.add(ta);
        leaveRatingDTO = new LeaveRatingDTO(ta.getId(), Optional.of(3));
        when(taRepository.findById(ta.getId())).thenReturn(Optional.ofNullable(ta));
    }

    @Test
    void getAverageRating_test() {
        // Act
        taController.getAverageRating(studentId);
        // Assert
        verify(taService).getAverageRating(studentId);
    }

    @Test
    public void getTAbyIdSuccessful() {
        when(taRepository.findById(ta.getId())).thenReturn(Optional.ofNullable(ta));
        Assertions.assertEquals(taController.getTAById(ta.getId()).block(), ta);
    }

    @Test
    public void getTAbyIdNotSuccessful() {
        when(taRepository.findById(ta.getId())).thenReturn(Optional.empty());
        Assertions.assertNull(taController.getTAById(ta.getId()).block());
    }

    @Test
    public void getAllTAs() {
        when(taRepository.findAll()).thenReturn(taList);
        Assertions.assertEquals(taController.getTAs().block(), taList);
    }

    @Test
    public void createTATest() {
        taController.createTA(ta.getStudentId(), ta.getCourseId());
        verify(taRepository).save(any(ta.getClass()));
    }

    @Test
    public void addRatingTest() {
        taController.createTA(ta.getStudentId(), ta.getCourseId());
        taController.addRating(leaveRatingDTO);
        verify(taService).addRating(leaveRatingDTO);
        verify(taRepository).save(any(ta.getClass()));
    }

    @Test
    public void addRatingBadRequest() {
        ResponseStatusException thrown =
                Assertions.assertThrows(ResponseStatusException.class, () -> {
                    leaveRatingDTO.setRating(Optional.empty());
                    taController.addRating(leaveRatingDTO);
                });
        Assertions.assertEquals("400 BAD_REQUEST \"Request is incomplete\"", thrown.getMessage());
    }

    @Test
    public void setRatingBadRequest() {
        ResponseStatusException thrown =
                Assertions.assertThrows(ResponseStatusException.class, () -> {
                    leaveRatingDTO.setRating(Optional.empty());
                    taController.setRating(leaveRatingDTO);
                });
        Assertions.assertEquals("400 BAD_REQUEST \"Request is incomplete\"", thrown.getMessage());
    }

    @Test
    public void setRatingSuccessful() {
        taController.createTA(ta.getStudentId(), ta.getCourseId());
        taController.setRating(leaveRatingDTO);
        verify(taService).addRating(leaveRatingDTO);
        verify(taRepository).save(any(ta.getClass()));
    }

    @Test
    public void deleteTest() {
        taController.deleteTA(ta.getId());
        verify(taRepository).deleteById(ta.getId());
    }

    @Test
    public void deleteFailed() {
        doThrow(IllegalArgumentException.class).when(taRepository).deleteById(null);
        Assertions.assertEquals(false, taController.deleteTA(null).block());
        verify(taRepository).deleteById(null);
    }
}
