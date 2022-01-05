package nl.tudelft.sem.Application.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.DTO.GradeDTO;
import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.DTO.RecommendationDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class CollectionServiceTests {
    @Autowired
    CollectionService collectionService;
    @MockBean
    ApplicationService applicationService;

    private final UUID studentId = UUID.randomUUID();
    private final UUID courseId = UUID.randomUUID();

    /**
     * collectApplicationDetails tests
     */
    @Test
    void collectApplicationDetails_successfully_returnsRecommendationDTO() throws Exception {
        // Arrange
        RecommendationDTO expected = new RecommendationDTO(studentId, Optional.of(4), 8.2d);
        //      Mock behaviour for retrieving RatingDTO
        Mockito.when(applicationService.getRatingForTA(studentId, 47110)).thenReturn(new RatingDTO(studentId, expected.getRating().get()));
        //      Mock behaviour for retrieving GradeDTO
        Mockito.when(applicationService.getGradeByCourseIdAndStudentId(courseId, studentId, 47112)).thenReturn(new GradeDTO(studentId, expected.getGrade()));
        // Act
        RecommendationDTO result = collectionService.collectApplicationDetails(courseId, studentId);
        // Assert
        assertEquals(expected, result);
    }

    @Test
    void collectApplicationDetails_noRating_returnsRecommendationDTOwithEmptyRating ()
        throws Exception {
        // Arrange
        RecommendationDTO expected = new RecommendationDTO(studentId, Optional.empty(), 8.2d);
        //      Mock behaviour for retrieving RatingDTO
        Mockito.when(applicationService.getRatingForTA(studentId, 47110)).thenReturn(new RatingDTO(studentId, null));
        //      Mock behaviour for retrieving GradeDTO
        Mockito.when(applicationService.getGradeByCourseIdAndStudentId(courseId, studentId, 47112)).thenReturn(new GradeDTO(studentId, expected.getGrade()));
        // Act
        RecommendationDTO result = collectionService.collectApplicationDetails(courseId, studentId);
        // Assert
        assertEquals(expected, result);
    }

    @Test
    void collectApplicationDetails_noGrade_throwsException() throws Exception {
        // Arrange
        RecommendationDTO expected = new RecommendationDTO(studentId, Optional.empty(), 8.2d);
        //      Mock behaviour for retrieving RatingDTO
        Mockito.when(applicationService.getRatingForTA(studentId, 47110)).thenThrow(new Exception("No TA rating found!"));
        //      Mock behaviour for retrieving GradeDTO
        Mockito.when(applicationService.getGradeByCourseIdAndStudentId(courseId, studentId, 47112)).thenThrow(new Exception("missing"));
        // Act && Assert
        Exception result = assertThrows(Exception.class, () ->
            collectionService.collectApplicationDetails(courseId, studentId)
        );
        // Assert
        assertEquals("Grade not present for student for this course", result.getMessage());
    }
}
