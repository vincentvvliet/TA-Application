package nl.tudelft.sem.Application.controllers;

import nl.tudelft.sem.Application.controllers.ApplicationController;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.ApplicationService;
import nl.tudelft.sem.Application.services.RecommendationService;
import nl.tudelft.sem.DTO.RecommendationDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class ApplicationControllerTests {

    @Autowired
    ApplicationController applicationController;

    @MockBean
    ApplicationService applicationService;

    @MockBean
    ApplicationRepository applicationRepository;

    @MockBean
    RecommendationService recommendationService;

    UUID student1Id = UUID.randomUUID();
    UUID student2Id = UUID.randomUUID();
    List<Application> list;
    Application app1;
    Application app2;
    List<Application> applicationList;
    UUID id;
    UUID courseId;
    UUID studentId;
    Application application;

    public final Application rec_app1 = new Application(UUID.randomUUID(), UUID.randomUUID());
    public final Application rec_app2 = new Application(UUID.randomUUID(), UUID.randomUUID());
    public final Application rec_app3 = new Application(UUID.randomUUID(), UUID.randomUUID());
    public final Application rec_app4NoGrade = new Application(UUID.randomUUID(), UUID.randomUUID());

    RecommendationDTO recommendation1 =
        new RecommendationDTO(rec_app1.getStudentId(), Optional.of(4), 8.8d);
    RecommendationDTO recommendation2 =
        new RecommendationDTO(rec_app2.getStudentId(), Optional.of(3), 7.5d);
    RecommendationDTO recommendation3 =
        new RecommendationDTO(rec_app3.getStudentId(), Optional.of(5), 6.5d);



    @BeforeEach
    public void init() {
        applicationList = new ArrayList<>();
        id = UUID.randomUUID();
        courseId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        application = new Application(courseId, studentId);
        applicationList.add(application);
        app1 = new Application(courseId, student1Id);
        app2 = new Application(courseId, student2Id);
        list = List.of(app1, app2);
    }

    @Test
    public void getApplicationsByCourseTest() {
        when(applicationService.getApplicationsByCourse(courseId)).thenReturn(applicationList);
        Flux<Application> flux = applicationController.getApplicationsByCourse(courseId);
        List<Application> result = flux.toStream().collect(Collectors.toList());
        Assertions.assertEquals(result, applicationList);
    }

    @Test
    public void createApplicationByStudentAndCourseIsNotValidTest() {
        when(applicationService.validate(any(Application.class))).thenReturn(false);
        Assertions.assertEquals(applicationController.createApplicationByStudentAndCourse(studentId,courseId).block(), false);
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void createApplicationByStudentAndCourseIsValidTest() {
        when(applicationService.validate(any(Application.class))).thenReturn(true);
        Assertions.assertEquals(applicationController.createApplicationByStudentAndCourse(studentId,courseId).block(), true);
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    public void acceptApplicationSuccessfully() throws Exception {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        when(applicationService.isTASpotAvailable(courseId, 47110)).thenReturn(true);
        when(applicationService.studentCanTAAnotherCourse(studentId, courseId)).thenReturn(true);
        when(applicationService.createTA(studentId,courseId, 47110)).thenReturn(true);

        Assertions.assertEquals(applicationController.acceptApplication(id).block(), true);
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    public void acceptApplicationAlreadyAccepted() {
        application.setAccepted(true);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        Exception exception = assertThrows(Exception.class, () -> applicationController.acceptApplication(id));
        String expectedMessage = "application is already accepted";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationNoApplicationInRepository() {
        when(applicationRepository.findById(id)).thenReturn(Optional.empty());
        Exception exception = assertThrows(NoSuchElementException.class, () -> applicationController.acceptApplication(id));
        String expectedMessage = "application does not exist";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationMaximumTAsReached() {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        when(applicationService.studentCanTAAnotherCourse(studentId, courseId)).thenReturn(true);
        when(applicationService.isTASpotAvailable(courseId, 47110)).thenReturn(false);

        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationController.acceptApplication(id));
        String expectedMessage = "maximum number of TA's was already reached for this course";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationAlreadyTAFor3CoursesPerQuarter() {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        when(applicationService.studentCanTAAnotherCourse(studentId, courseId)).thenReturn(false);
        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationController.acceptApplication(id));
        String expectedMessage = "a student can TA a maximum of 3 courses per quarter";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationCreationFault() throws Exception {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        when(applicationService.isTASpotAvailable(courseId, 47110)).thenReturn(true);
        when(applicationService.studentCanTAAnotherCourse(studentId, courseId)).thenReturn(true);
        when(applicationService.createTA(studentId,courseId, 47110)).thenReturn(false);

        Assertions.assertEquals(applicationController.acceptApplication(id).block(), false);
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    void getApplication_existent_returnsApplication() {
        // Arrange
        when(applicationRepository.findByStudentIdAndCourseId(student1Id, courseId)).thenReturn(Optional.of(app1));
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
        when(applicationService.getApplicationsByCourse(courseId)).thenReturn(list);
        // Act
        Flux<Application> flux = applicationController.getApplicationsByCourse(courseId);
        // Assert
        List<Application> result = flux.toStream().collect(Collectors.toList());
        assertEquals(list, result);
    }

    @Test
    void getApplications_none_returnsEmptyList() {
        // Act
        UUID random = UUID.randomUUID();
        when(applicationRepository.findApplicationsByCourseId(random)).thenReturn(List.of());
        Flux<Application> flux = applicationController.getApplicationsByCourse(courseId);
        List<Application> result = flux.toStream().collect(Collectors.toList());
        // Assert
        assertEquals(List.of(), result);
    }

    /**
     * getSortedList tests
     */
    @Test
    void getSortedList_successfulList_returnsFlux() throws Exception {
        // Arrange
        UUID courseId = UUID.randomUUID();
        //      Set Service behaviour
        when(recommendationService.getRecommendationDetailsByCourse(any()))
            .thenReturn(List.of(recommendation1, recommendation2, recommendation3));
        when(recommendationService.sortOnStrategy(any(), any()))
            .thenReturn(List.of(recommendation3, recommendation1, recommendation2));
        // Act
        Flux<RecommendationDTO> result = applicationController
            .getSortedList(courseId, "IgnoreGrade");
        // Assert
        assertEquals(List.of(recommendation3, recommendation1, recommendation2), result.collectList().block());
    }

    @Test
    void getSortedList_unknownStrategy_throwsException() throws Exception {
        // Arrange
        UUID courseId = UUID.randomUUID();
        //      Set Service behaviour
        when(recommendationService.getRecommendationDetailsByCourse(any()))
            .thenReturn(List.of(recommendation1, recommendation2, recommendation3));
        when(recommendationService.recommendNStudents(any(), any(), anyInt()))
            .thenThrow(new Exception());
        // Act
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
            ()-> applicationController.hireN(courseId, "Gibberish", 2));
        // Assert
        assertEquals("Requested Strategy not found!", e.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
    }

    /**
     * recommendN tests
     */
    @Test
    void recommendN_successfulList_returnsFlux() throws Exception {
        // Arrange
        UUID courseId = UUID.randomUUID();
        //      Set Service behaviour
        when(recommendationService.getRecommendationDetailsByCourse(any()))
            .thenReturn(List.of(recommendation1, recommendation2, recommendation3));
        when(recommendationService.recommendNStudents(any(), any(), anyInt()))
            .thenReturn(List.of(recommendation3, recommendation1));
        // Act
        Flux<RecommendationDTO> result = applicationController.recommendN(courseId, "IgnoreGrade", 2);
        // Assert
        assertEquals(List.of(recommendation3, recommendation1), result.collectList().block());
    }

    @Test
    void recommendN_unknownStrategy_throwsException() throws Exception {
        // Arrange
        UUID courseId = UUID.randomUUID();
        //      Set Service behaviour
        when(recommendationService.getRecommendationDetailsByCourse(any()))
            .thenReturn(List.of(recommendation1, recommendation2, recommendation3));
        when(recommendationService.recommendNStudents(any(), any(), anyInt()))
            .thenThrow(new Exception());
        // Act
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
            ()-> applicationController.recommendN(courseId, "Gibberish", 2));
        // Assert
        assertEquals("Requested Strategy not found!", e.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
    }

    /**
     * hireN tests
     */
    @Test
    void hireN_ExistantStrategy_makeHiringRequests() throws Exception {
        // Arrange
        UUID courseId = UUID.randomUUID();
        //      Set Service behaviour
        when(recommendationService.getRecommendationDetailsByCourse(any()))
            .thenReturn(List.of(recommendation1, recommendation2, recommendation3));
        when(recommendationService.recommendNStudents(any(), any(), anyInt()))
            .thenReturn(List.of(recommendation3, recommendation1));
        when(applicationService.isTASpotAvailable(any()))
            .thenReturn(true);
        when(applicationService.createTA(any(), any()))
            .thenReturn(true);
        //      Set Database behaviour
        when(applicationRepository.findByStudentIdAndCourseId(eq(recommendation1.getStudentId()), any()))
            .thenReturn(Optional.of(rec_app1));
        when(applicationRepository.findByStudentIdAndCourseId(eq(recommendation2.getStudentId()), any()))
            .thenReturn(Optional.of(rec_app2));
        when(applicationRepository.findByStudentIdAndCourseId(eq(recommendation3.getStudentId()), any()))
            .thenReturn(Optional.of(rec_app3));
        // Act
        Mono<Boolean> result = applicationController.hireN(courseId, "IgnoreGrade", 2);
        // Assert
        assertEquals(true, result.block());
        verify(applicationRepository).findByStudentIdAndCourseId(eq(recommendation1.getStudentId()), any());
        verify(applicationRepository).findByStudentIdAndCourseId(eq(recommendation3.getStudentId()), any());
        verify(applicationService, atMostOnce()).createTA(eq(recommendation1.getStudentId()), any());
        verify(applicationService, never()).createTA(eq(recommendation2.getStudentId()), any());
        verify(applicationService, atMostOnce()).createTA(eq(recommendation3.getStudentId()), any());

    }

    @Test
    void hireN_unknownStrategy_throwsException() throws Exception {
        // Arrange
        UUID courseId = UUID.randomUUID();
        //      Set Service behaviour
        when(recommendationService.getRecommendationDetailsByCourse(any()))
            .thenReturn(List.of(recommendation1, recommendation2, recommendation3));
        when(recommendationService.recommendNStudents(any(), any(), anyInt()))
            .thenThrow(new Exception());
        // Act
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
            ()-> applicationController.hireN(courseId, "Gibberish", 2));
        // Assert
        assertEquals("Requested Strategy not found!", e.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
    }

    @Test
    void hireN_faultyRecommendationDTO_throwsException() throws Exception {
        // Arrange
        UUID courseId = UUID.randomUUID();
        //      Set Service behaviour
        when(recommendationService.getRecommendationDetailsByCourse(any()))
            .thenReturn(List.of(recommendation1, recommendation2, recommendation3));
        when(recommendationService.recommendNStudents(any(), any(), anyInt()))
            .thenReturn(List.of(recommendation3, recommendation1));
        when(applicationService.isTASpotAvailable(any()))
            .thenReturn(true);
        when(applicationService.createTA(any(), any()))
            .thenReturn(true);
        //      Set Database behaviour
        when(applicationRepository.findByStudentIdAndCourseId(any(), any())).thenReturn(Optional.empty());
        // Act
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
            ()-> applicationController.hireN(courseId, "IgnoreGrade", 2));
        // Assert
        assertEquals("StudentId in recommendationDTO not found!", e.getReason());
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    }

}
