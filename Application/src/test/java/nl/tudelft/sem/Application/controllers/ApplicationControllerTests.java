package nl.tudelft.sem.Application.controllers;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.exceptions.EmptyResourceException;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.ApplicationService;
import nl.tudelft.sem.Application.services.RecommendationService;
import nl.tudelft.sem.DTO.ApplicationDTO;
import nl.tudelft.sem.DTO.ApplyingStudentDTO;
import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.DTO.RecommendationDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    LocalDate startDateOpen;
    LocalDate startDateClosed;

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
        startDateOpen = LocalDate.now().plusWeeks(4);
        startDateClosed = LocalDate.now().plusWeeks(2);
    }

    @Test
    public void getApplicationsByCourseTest() {
        when(applicationService.getApplicationsByCourse(courseId)).thenReturn(applicationList);
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
    public void removeApplicationSuccessfullyTest() {
        when(applicationService.removeApplication(studentId,courseId)).thenReturn(true);
        Assertions.assertTrue(applicationController.removeApplication(studentId,courseId).block());
    }

    @Test
    public void removeApplicationErrorTest() {
        when(applicationService.removeApplication(studentId,courseId)).thenReturn(false);
        Assertions.assertFalse(applicationController.removeApplication(studentId,courseId).block());
    }

    @Test
    public void acceptApplicationSuccessfully() throws Exception {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        when(applicationService.isSelectionPeriodOpen(startDateClosed)).thenReturn(true);
        when(applicationService.studentCanTAAnotherCourse(studentId,courseId, 47112)).thenReturn(true);
        when(applicationService.isTASpotAvailable(courseId, 47112)).thenReturn(true);
        when(applicationService.getCourseStartDate(courseId,47112)).thenReturn(startDateClosed);
        when(applicationService.acceptApplication(application)).thenReturn(Mono.just(true));
        Assertions.assertEquals(true, applicationController.acceptApplication(id).block());
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
    public void acceptApplicationMaximumTAsReached() throws EmptyResourceException {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        when(applicationService.studentCanTAAnotherCourse(studentId, courseId, 47112)).thenReturn(true);
        when(applicationService.isTASpotAvailable(courseId, 47110)).thenReturn(false);
        when(applicationService.getCourseStartDate(courseId, 47112)).thenReturn(startDateClosed);
        when(applicationService.isSelectionPeriodOpen(startDateClosed)).thenReturn(true);


        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationController.acceptApplication(id));
        String expectedMessage = "maximum number of TA's was already reached for this course";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationAlreadyTAFor3CoursesPerQuarter() throws EmptyResourceException {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        when(applicationService.studentCanTAAnotherCourse(studentId, courseId, 47112)).thenReturn(false);
        when(applicationService.getCourseStartDate(courseId, 47112)).thenReturn(startDateClosed);
        when(applicationService.isSelectionPeriodOpen(startDateClosed)).thenReturn(true);
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
        when(applicationService.studentCanTAAnotherCourse(studentId, courseId, 47112)).thenReturn(true);
        when(applicationService.getCourseStartDate(courseId, 47112)).thenReturn(startDateClosed);
        when(applicationService.isSelectionPeriodOpen(startDateClosed)).thenReturn(true);
        when(applicationService.isTASpotAvailable(courseId, 47112)).thenReturn(true);
        when(applicationService.acceptApplication(application)).thenReturn(Mono.just(false));

        assertEquals(false, applicationController.acceptApplication(id).block());
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationSelectionPeriodIsClosed() throws Exception {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        when(applicationService.getCourseStartDate(courseId, 47112)).thenReturn(startDateClosed);
        when(applicationService.isSelectionPeriodOpen(startDateClosed)).thenReturn(false);
        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationController.acceptApplication(id));
        String expectedMessage = "TA selection period is not open now";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
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

    /** Tests for getSortedListWithMinimumGrade
     *
     */

    @Test
    void getSortedListWithMinimumGradeSuccessful() throws Exception {
        when(recommendationService.getRecommendationDetailsByCourse(courseId))
                .thenReturn(List.of(recommendation1,recommendation2,recommendation3));
        when(recommendationService.sortOnStrategy(List.of(recommendation1,recommendation2),"Strategy" ))
                .thenReturn(List.of(recommendation2,recommendation1));

        Flux<RecommendationDTO> result = applicationController
                .getSortedListWithMinimumGrade(courseId, "Strategy", 7.0);
        // Assert
        assertEquals(List.of(recommendation2, recommendation1), result.collectList().block());

    }

    @Test
    void getSortedListWithMinimumGradeNoGradesPass() throws Exception {
        when(recommendationService.getRecommendationDetailsByCourse(courseId))
                .thenReturn(List.of(recommendation1,recommendation2,recommendation3));
        when(recommendationService.sortOnStrategy(List.of(),"Strategy" ))
                .thenReturn(List.of());

        Flux<RecommendationDTO> result = applicationController
                .getSortedListWithMinimumGrade(courseId, "Strategy", 9.0);
        // Assert
        assertEquals(List.of(), result.collectList().block());

        verify(recommendationService).sortOnStrategy(List.of(),"Strategy");

    }

    @Test
    void getSortedListWithMinimumGradeAllGradesPass() throws Exception {
        when(recommendationService.getRecommendationDetailsByCourse(courseId))
                .thenReturn(List.of(recommendation1,recommendation2,recommendation3));
        when(recommendationService.sortOnStrategy(List.of(recommendation1,recommendation2,recommendation3),"Strategy" ))
                .thenReturn(List.of(recommendation1,recommendation2,recommendation3));

        Flux<RecommendationDTO> result = applicationController
                .getSortedListWithMinimumGrade(courseId, "Strategy", 6.0);
        // Assert
        assertEquals(List.of(recommendation1,recommendation2,recommendation3), result.collectList().block());

        verify(recommendationService).sortOnStrategy(List.of(recommendation1,recommendation2,recommendation3),"Strategy");

    }

    @Test
    void getSortedListWithMinimumGradeMinimumEqualsGrade() throws Exception {
        when(recommendationService.getRecommendationDetailsByCourse(courseId))
                .thenReturn(List.of(recommendation1,recommendation2,recommendation3));
        when(recommendationService.sortOnStrategy(List.of(recommendation1),"Strategy" ))
                .thenReturn(List.of(recommendation1));

        Flux<RecommendationDTO> result = applicationController
                .getSortedListWithMinimumGrade(courseId, "Strategy", 8.8);
        // Assert
        assertEquals(List.of(recommendation1), result.collectList().block());

        verify(recommendationService).sortOnStrategy(List.of(recommendation1),"Strategy");

    }

    @Test
    void getSortedListWithMinimumGradeExceptionThrown() throws Exception {
        when(recommendationService.getRecommendationDetailsByCourse(courseId))
                .thenReturn(List.of(recommendation1,recommendation2,recommendation3));
        when(recommendationService.sortOnStrategy(List.of(recommendation1,recommendation2),"Strategy" ))
                .thenThrow(new Exception());

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                ()-> applicationController.getSortedListWithMinimumGrade(courseId, "Strategy", 7.0));

        assertEquals("Requested Strategy not found!", e.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());

    }

    /**
     * Tests for recommendNStudentsWithMinimumGrade.
     */

    @Test
    void recommendNStudentsWithMinimumGradeSuccessful() throws Exception {
        when(recommendationService.getRecommendationDetailsByCourse(courseId))
                .thenReturn(List.of(recommendation1,recommendation2,recommendation3));
        when(recommendationService.recommendNStudents(List.of(recommendation1,recommendation2),"Strategy" , 3))
                .thenReturn(List.of(recommendation2,recommendation1));

        Flux<RecommendationDTO> result = applicationController
                .recommendNStudentsWithMinimumGrade(courseId, "Strategy", 3,7.0);
        // Assert
        assertEquals(List.of(recommendation2, recommendation1), result.collectList().block());

    }

    @Test
    void recommendNStudentsWithMinimumGradeNoGradesPass() throws Exception {
        when(recommendationService.getRecommendationDetailsByCourse(courseId))
                .thenReturn(List.of(recommendation1,recommendation2,recommendation3));
        when(recommendationService.recommendNStudents(List.of(),"Strategy", 3 ))
                .thenReturn(List.of());

        Flux<RecommendationDTO> result = applicationController
                .recommendNStudentsWithMinimumGrade(courseId, "Strategy", 3, 9.0);
        // Assert
        assertEquals(List.of(), result.collectList().block());

        verify(recommendationService).recommendNStudents(List.of(),"Strategy", 3);

    }

    @Test
    void recommendNStudentsWithMinimumGradeAllGradesPass() throws Exception {
        when(recommendationService.getRecommendationDetailsByCourse(courseId))
                .thenReturn(List.of(recommendation1,recommendation2,recommendation3));
        when(recommendationService.recommendNStudents(List.of(recommendation1,recommendation2,recommendation3),"Strategy", 3 ))
                .thenReturn(List.of(recommendation1,recommendation2,recommendation3));

        Flux<RecommendationDTO> result = applicationController
                .recommendNStudentsWithMinimumGrade(courseId, "Strategy", 3, 6.0);
        // Assert
        assertEquals(List.of(recommendation1,recommendation2,recommendation3), result.collectList().block());

        verify(recommendationService).recommendNStudents(List.of(recommendation1,recommendation2,recommendation3),"Strategy", 3);

    }

    @Test
    void recommendNStudentsWithMinimumGradeMinimumEqualsGrade() throws Exception {
        when(recommendationService.getRecommendationDetailsByCourse(courseId))
                .thenReturn(List.of(recommendation1,recommendation2,recommendation3));
        when(recommendationService.recommendNStudents(List.of(recommendation1),"Strategy", 3 ))
                .thenReturn(List.of(recommendation1));

        Flux<RecommendationDTO> result = applicationController
                .recommendNStudentsWithMinimumGrade(courseId, "Strategy", 3, 8.8);
        // Assert
        assertEquals(List.of(recommendation1), result.collectList().block());

        verify(recommendationService).recommendNStudents(List.of(recommendation1),"Strategy", 3);

    }

    @Test
    void recommendNStudentsWithMinimumGradeExceptionThrown() throws Exception {
        when(recommendationService.getRecommendationDetailsByCourse(courseId))
                .thenReturn(List.of(recommendation1,recommendation2,recommendation3));
        when(recommendationService.recommendNStudents(List.of(recommendation1,recommendation2),"Strategy", 3 ))
                .thenThrow(new Exception());

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                ()-> applicationController.recommendNStudentsWithMinimumGrade(courseId, "Strategy", 3, 7.0));

        assertEquals("Requested Strategy not found!", e.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());

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
        when(recommendationService.sortOnStrategy(any(), any()))
            .thenThrow(new Exception());
        // Act
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
            ()-> applicationController.getSortedList(courseId, "IgnoreGrade"));
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
        Flux<RecommendationDTO> result = applicationController.recommendNStudents(courseId, "IgnoreGrade", 2);
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
            ()-> applicationController.recommendNStudents(courseId, "Gibberish", 2));
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
        when(applicationService.isTASpotAvailable(any(), eq(47110)))
            .thenReturn(true);
        when(applicationService.createTA(any(), any(), eq(47110)))
            .thenReturn(true);
        //      Set Database behaviour
        when(applicationRepository.findByStudentIdAndCourseId(eq(recommendation1.getStudentId()), any()))
            .thenReturn(Optional.of(rec_app1));
        when(applicationRepository.findByStudentIdAndCourseId(eq(recommendation2.getStudentId()), any()))
            .thenReturn(Optional.of(rec_app2));
        when(applicationRepository.findByStudentIdAndCourseId(eq(recommendation3.getStudentId()), any()))
            .thenReturn(Optional.of(rec_app3));
        // Act
        Mono<Boolean> result = applicationController.hireNStudents(courseId, "IgnoreGrade", 2);
        // Assert
        assertEquals(true, result.block());
        verify(applicationRepository).findByStudentIdAndCourseId(eq(recommendation1.getStudentId()), any());
        verify(applicationRepository).findByStudentIdAndCourseId(eq(recommendation3.getStudentId()), any());
        verify(applicationService, atMostOnce()).createTA(eq(recommendation1.getStudentId()), any(), eq(47110));
        verify(applicationService, never()).createTA(eq(recommendation2.getStudentId()), any(), eq(47110));
        verify(applicationService, atMostOnce()).createTA(eq(recommendation3.getStudentId()), any(), eq(47110));

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
            ()-> applicationController.hireNStudents(courseId, "Gibberish", 2));
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
        when(applicationService.isTASpotAvailable(any(), eq(47110)))
            .thenReturn(true);
        when(applicationService.createTA(any(), any(), eq(47110)))
            .thenReturn(true);
        //      Set Database behaviour
        when(applicationRepository.findByStudentIdAndCourseId(any(), any())).thenReturn(Optional.empty());
        // Act
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
            ()-> applicationController.hireNStudents(courseId, "IgnoreGrade", 2));
        // Assert
        assertEquals("StudentId in recommendationDTO not found!", e.getReason());
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    }

    @Test
    void hireN_skipAlreadyAccepted() throws Exception {
        // Arrange
        UUID courseId = UUID.randomUUID();
        //      Set Service behaviour
        when(recommendationService.getRecommendationDetailsByCourse(any()))
            .thenReturn(List.of(recommendation1, recommendation2, recommendation3));
        when(recommendationService.recommendNStudents(any(), any(), anyInt()))
            .thenReturn(List.of(recommendation1));
        when(applicationService.isTASpotAvailable(any(), eq(47110)))
            .thenReturn(true);
        when(applicationService.createTA(any(), any(), eq(47110)))
            .thenReturn(true);
        Application acceptedApp = new Application(courseId, studentId);
        acceptedApp.setAccepted(true);
        //      Set Database behaviour
        when(applicationRepository.findByStudentIdAndCourseId(any(), any())).thenReturn(Optional.of(acceptedApp));
        // Assert
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void hireN_skipsTASpotNotAvailable() throws Exception {
        // Arrange
        UUID courseId = UUID.randomUUID();
        //      Set Service behaviour
        when(recommendationService.getRecommendationDetailsByCourse(any()))
            .thenReturn(List.of(recommendation1, recommendation2, recommendation3));
        when(recommendationService.recommendNStudents(any(), any(), anyInt()))
            .thenReturn(List.of(recommendation1));
        when(applicationService.isTASpotAvailable(any(), eq(47110)))
            .thenReturn(false);
        //      Set Database behaviour
        when(applicationRepository.findByStudentIdAndCourseId(any(), any()))
            .thenReturn(Optional.of(rec_app1));
        // Assert
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void hireN_skipsNotSuccesfullyCreated() throws Exception {
        // Arrange
        UUID courseId = UUID.randomUUID();
        //      Set Service behaviour
        when(recommendationService.getRecommendationDetailsByCourse(any()))
            .thenReturn(List.of(recommendation1, recommendation2, recommendation3));
        when(recommendationService.recommendNStudents(any(), any(), anyInt()))
            .thenReturn(List.of(recommendation1));
        when(applicationService.isTASpotAvailable(any(), eq(47110)))
            .thenReturn(true);
        when(applicationService.createTA(any(), any(), eq(47110)))
            .thenReturn(false);
        //      Set Database behaviour
        when(applicationRepository.findByStudentIdAndCourseId(any(), any()))
            .thenReturn(Optional.of(rec_app1));
        // Assert
        verify(applicationRepository, never()).save(any());
    }

    /**
     * getApplicationsOverviewByCourse tests
     */
    @Test
    void getApplicationsOverviewByCourse_serviceSucces_returnsFlux() throws Exception {
        // Arrange
        List<Application> list = List.of(
            new Application(courseId, UUID.randomUUID()),
            new Application(courseId, UUID.randomUUID()),
            new Application(courseId, UUID.randomUUID())
        );
        List<ApplyingStudentDTO> applying = List.of(
            new ApplyingStudentDTO(list.get(0).getStudentId(), 8.0d, Optional.of(4)),
            new ApplyingStudentDTO(list.get(1).getStudentId(), 6.6d, Optional.empty()),
            new ApplyingStudentDTO(list.get(2).getStudentId(), 8.9d, Optional.of(5))
        );
        when(applicationRepository.findApplicationsByCourseId(any()))
            .thenReturn(list);
        when(applicationService.getApplicationDetails(any(), anyInt(), anyInt()))
            .thenReturn(applying);
        // Act
        Flux<ApplyingStudentDTO> result = applicationController.getApplicationsOverviewByCourse(courseId);
        // Assert
        assertEquals(applying, result.toStream().collect(Collectors.toList()));
    }

    @Test
    void getApplicationsOverviewByCourse_serviceFailure_throwsException() throws Exception {
        // Arrange
        when(applicationRepository.findApplicationsByCourseId(any()))
            .thenReturn(List.of());
        when(applicationService.getApplicationDetails(any(), anyInt(), anyInt()))
            .thenThrow(new Exception("Massive error here!"));
        // Act
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () ->
            applicationController.getApplicationsOverviewByCourse(courseId)
        );
        // Assert
        assertEquals("No applications found!", e.getReason());
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    }

    /**
     * getRating tests
     */
    @Test
    void getRating_serviceSucces_returnsRatingDTO() throws Exception {
        // Arrange
        RatingDTO dto = new RatingDTO(studentId, 3);
        when(applicationService.getRatingForTA(any(), anyInt()))
            .thenReturn(dto);
        // Act
        RatingDTO result = applicationController.getRating(studentId);
        // Assert
        assertEquals(result, dto);
    }

    @Test
    void getRating_serviceFails_returnsNull() throws Exception {
        // Arrange
        RatingDTO dto = new RatingDTO(studentId, 3);
        when(applicationService.getRatingForTA(any(), anyInt()))
            .thenThrow(new Exception("Massive error here!"));
        // Act
        RatingDTO result = applicationController.getRating(studentId);
        // Assert
        assertEquals(result, null);
    }


}
