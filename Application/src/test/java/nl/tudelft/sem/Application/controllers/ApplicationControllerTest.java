package nl.tudelft.sem.Application.controllers;

import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ApplicationControllerTest {
    @InjectMocks
    ApplicationController applicationController;

    @Mock
    ApplicationRepository applicationRepository;

    UUID student1Id = UUID.randomUUID();
    UUID student2Id = UUID.randomUUID();
    UUID courseId = UUID.randomUUID();
    Application app1 = new Application(courseId, student1Id);
    Application app2 = new Application(courseId, student2Id);
    List<Application> list = List.of(app1, app2);

    @BeforeEach
    void init() {
        when(applicationRepository.findByStudentIdAndCourseId(student1Id, courseId)).
            thenReturn(Optional.ofNullable(app1));
        when(applicationRepository.findApplicationsByCourseId(courseId))
            .thenReturn(list);
    }

    @Test
    void getApplication_existent_returnsApplication() {
        // Act
        Optional<Application> result =
            applicationController.getApplication(student1Id, courseId);
        // Assert
        assertTrue(result.isPresent());
        assertEquals(app1, result.get());
    }

    @Test
    void getApplication_nonExistent_returnsNothing() {
        // Arrange
        UUID nonexistent = UUID.randomUUID();
        when(applicationRepository.findByStudentIdAndCourseId(nonexistent, courseId))
            .thenReturn(Optional.empty());
        // Act
        Optional<Application> result = applicationController.getApplication(nonexistent, courseId);
        // Assert
        verify(applicationRepository).findByStudentIdAndCourseId(nonexistent, courseId);
        assertTrue(result.isEmpty());
    }

    @Test
    void getApplications_twoCompatible_returnsBoth() {
        // Act
        List<Application> result = applicationController.getApplicationsByCourse(courseId);
        // Assert
        verify(applicationRepository).findApplicationsByCourseId(courseId);
        assertEquals(list, result);
    }

    @Test
    void getApplications_none_returnsEmptyList() {
        // Act
        UUID random = UUID.randomUUID();
        when(applicationRepository.findApplicationsByCourseId(random)).thenReturn(List.of());
        List<Application> result = applicationController.getApplicationsByCourse(random);
        // Assert
        assertEquals(List.of(), result);
    }
}
