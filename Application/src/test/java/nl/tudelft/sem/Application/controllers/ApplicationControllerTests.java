package nl.tudelft.sem.Application.controllers;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.ApplicationService;
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
import reactor.core.publisher.Flux;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void acceptApplicationSuccessfully() throws Exception {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        when(applicationService.isTASpotAvailable(courseId, 47110)).thenReturn(true);
        //when(applicationService.createTA(studentId,courseId,47110)).thenReturn(true);
        when(applicationService.getCourseStartDate(courseId,47110)).thenReturn(startDateClosed);
        Assertions.assertEquals(applicationController.acceptApplication(id).block(), true);
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    public void acceptApplicationAlreadyAccepted() {
        application.setAccepted(true);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationController.acceptApplication(id));
        String expectedMessage = "application is already accepted";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationNoApplicationInRepository() {
        when(applicationRepository.findById(id)).thenReturn(Optional.empty());
        Exception exception = Assertions.assertThrows(NoSuchElementException.class, () -> applicationController.acceptApplication(id));
        String expectedMessage = "application does not exist";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationMaximumTAsReached() throws Exception {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        when(applicationService.isTASpotAvailable(courseId, 47110)).thenReturn(false);
        when(applicationService.getCourseStartDate(courseId, 47110)).thenReturn(startDateClosed);
        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationController.acceptApplication(id));
        String expectedMessage = "maximum number of TA's was already reached for this course";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationCreationFault() throws Exception {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        when(applicationService.isTASpotAvailable(courseId, 47110)).thenReturn(true);
        when(applicationService.getCourseStartDate(courseId, 47110)).thenReturn(startDateClosed);
        when(applicationService.createTA(studentId, courseId, 47110)).thenThrow(new Exception("Could not create TA."));
        Exception thrown =
                Assertions.assertThrows(Exception.class, () -> {
                    applicationController.acceptApplication(id);
                });
        Assertions.assertEquals("TA or contract creation failed: Could not create TA.", thrown.getMessage());
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

    @Test
    void acceptApplicationCourseStillOpen() throws Exception {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        when(applicationService.isTASpotAvailable(courseId, 47110)).thenReturn(true);
        //when(applicationService.createTA(studentId,courseId, 47110)).thenReturn(true);
        when(applicationService.getCourseStartDate(courseId, 47110)).thenReturn(startDateOpen);
        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationController.acceptApplication(id));
        String expectedMessage = "application is still open for application";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    void acceptApplicationCourseHasStarted() throws Exception {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        when(applicationService.isTASpotAvailable(courseId, 47110)).thenReturn(true);
        //when(applicationService.createTA(studentId,courseId, 47110).thenReturn(true);
        when(applicationService.getCourseStartDate(courseId, 47110)).thenReturn(LocalDate.now().minusWeeks(1));
        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationController.acceptApplication(id));
        String expectedMessage = "course has already started";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }

}
