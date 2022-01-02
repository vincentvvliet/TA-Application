package nl.tudelft.sem.Application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.DTO.RecommendationDTO;
import org.assertj.core.util.VisibleForTesting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class RecommendationServiceTests {
    @Autowired
    RecommendationService recommendationService;
    @MockBean
    CollectionService collectionService;
    @MockBean
    ApplicationRepository applicationRepository;

    Application app1 = new Application(UUID.randomUUID(), UUID.randomUUID());
    Application app2 = new Application(UUID.randomUUID(), UUID.randomUUID());
    Application app3 = new Application(UUID.randomUUID(), UUID.randomUUID());
    Application app4NoGrade = new Application(UUID.randomUUID(), UUID.randomUUID());

    RecommendationDTO recommendation1 =
        new RecommendationDTO(app1.getStudentId(), Optional.of(4), 8.8d);
    RecommendationDTO recommendation2 =
        new RecommendationDTO(app2.getStudentId(), Optional.of(3), 7.5d);
    RecommendationDTO recommendation3 =
        new RecommendationDTO(app3.getStudentId(), Optional.of(5), 6.5d);


    @BeforeEach
    void setup() throws Exception {
        when(collectionService.collectApplicationDetails(app1.getCourseId(), app1.getStudentId()))
            .thenReturn(recommendation1);
        when(collectionService.collectApplicationDetails(app2.getCourseId(), app2.getStudentId()))
            .thenReturn(recommendation2);
        when(collectionService.collectApplicationDetails(app3.getCourseId(), app3.getStudentId()))
            .thenReturn(recommendation3);
        when(collectionService.collectApplicationDetails(app4NoGrade.getCourseId(), app4NoGrade.getStudentId()))
            .thenThrow(new Exception("missing grade"));
    }

    /**
     * getRecommendationDetailsByCourse tests
     */
    @Test
    void getRecommendationDetailsByCourse_succes_noApplicaitons() {
        // Arrange
        when(applicationRepository.findApplicationsByCourseId(any())).thenReturn(List.of());
        // Act
        List<RecommendationDTO> result = recommendationService.getRecommendationDetailsByCourse(UUID.randomUUID());
        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getRecommendationDetailsByCourse_succes_multipleApplications() {
        // Arrange
        when(applicationRepository.findApplicationsByCourseId(any()))
            .thenReturn(List.of(app1, app2, app3));
        // Act
        List<RecommendationDTO> result = recommendationService.getRecommendationDetailsByCourse(UUID.randomUUID());
        // Assert
        assertEquals(List.of(recommendation1, recommendation2, recommendation3), result);
    }

    @Test
    void getRecommendationDetailsByCourse_succes_skips_gradeless_stuffs() {
        // Arrange
        when(applicationRepository.findApplicationsByCourseId(any()))
            .thenReturn(List.of(app1, app2, app4NoGrade));
        // Act
        List<RecommendationDTO> result = recommendationService.getRecommendationDetailsByCourse(UUID.randomUUID());
        // Assert
        assertEquals(List.of(recommendation1, recommendation2), result);
    }

    /**
     * sortOnStrategy tests
     */
    @Test
    void sortOnStrategy_ignoreRating_returnsSorted() throws Exception {
        // Act
        List<RecommendationDTO> result = recommendationService.sortOnStrategy(List.of(recommendation1, recommendation2, recommendation3), "IgnoreRating");
        // Assert
        assertEquals(List.of(recommendation1, recommendation2, recommendation3), result);
    }

    @Test
    void sortOnStrategy_ignoreGrade_returnsSorted() throws Exception {
        // Act
        List<RecommendationDTO> result = recommendationService.sortOnStrategy(List.of(recommendation1, recommendation2, recommendation3), "IgnoreGrade");
        // Assert
        assertEquals(List.of(recommendation3, recommendation1, recommendation2), result);
    }

    @Test
    void sortOnStrategy_useBoth_returnsSorted() throws Exception {
        // Act
        List<RecommendationDTO> result = recommendationService.sortOnStrategy(List.of(recommendation1, recommendation2, recommendation3), "Grade&Rating");
        // Assert
        assertEquals(List.of(recommendation1, recommendation2, recommendation3), result);
    }

    @Test
    void sortOnStrategy_unknownStrat_throwException() {
        // Act & Assert
        Exception e = assertThrows(Exception.class, () -> recommendationService
            .sortOnStrategy(List.of(recommendation1), "Nonexistant"));
        // Assert
        assertEquals("strategy doesn't exist!", e.getMessage());
    }

    /**
     * recommendNStudents tests
     */
    @Test
    void recommendNStudents_successful_returnsNHighestStudents() throws Exception {
        // Act
        List<RecommendationDTO> result = recommendationService.recommendNStudents(List.of(recommendation1, recommendation2, recommendation3), "IgnoreGrade", 2);
        // Assert
        assertEquals(List.of(recommendation3, recommendation1), result);
        assertEquals(2, result.size());
    }
}
