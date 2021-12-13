package nl.tudelft.sem.Application;

import nl.tudelft.sem.Application.controllers.ApplicationController;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.ApplicationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ApplicationControllerTests {

    @InjectMocks
    ApplicationController applicationController;

    @Mock
    ApplicationService applicationService;

    @Mock
    ApplicationRepository applicationRepository;

    List<Application> applicationList;
    UUID id;
    UUID courseId;
    UUID studentId;
    Application application;

    @BeforeEach
    public void init() {
        applicationList = new ArrayList<>();
        id = UUID.randomUUID();
        courseId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        application = new Application(courseId, studentId);
        applicationList.add(application);
    }

    @Test
    public void getApplicationsByCourseTest() {
        when(applicationService.getApplicationsByCourse(courseId)).thenReturn(applicationList);
        Assertions.assertEquals(applicationController.getApplicationsByCourse(courseId), applicationList);
    }

    @Test
    public void createApplicationByStudentAndCourseIsNotValidTest() {
        when(applicationService.validate(any(Application.class))).thenReturn(false);
        Assertions.assertEquals(applicationController.createApplicationByStudentAndCourse(studentId,courseId), false);
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void createApplicationByStudentAndCourseIsValidTest() {
        when(applicationService.validate(any(Application.class))).thenReturn(true);
        Assertions.assertEquals(applicationController.createApplicationByStudentAndCourse(studentId,courseId), true);
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    public void acceptApplicationSuccessfully() throws Exception {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        when(applicationService.isTASpotAvailable(courseId)).thenReturn(true);
        when(applicationService.createTA(studentId,courseId)).thenReturn(true);
        Assertions.assertEquals(applicationController.acceptApplication(id), true);
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    public void acceptApplicationAlreadyAccepted() throws Exception {
        application.setAccepted(true);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            applicationController.acceptApplication(id);
        });
        String expectedMessage = "application is already accepted";
        String actualMessage = exception.getMessage();

        Assertions.assertEquals(actualMessage.contains(expectedMessage),true);
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationNoApplicationInRepository() throws Exception {
        when(applicationRepository.findById(id)).thenReturn(Optional.empty());
        Exception exception = Assertions.assertThrows(NoSuchElementException.class, () -> {
            applicationController.acceptApplication(id);
        });
        String expectedMessage = "application does not exist";
        String actualMessage = exception.getMessage();

        Assertions.assertEquals(actualMessage.contains(expectedMessage),true);
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationMaximumTAsReached() throws Exception {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        when(applicationService.isTASpotAvailable(courseId)).thenReturn(false);
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            applicationController.acceptApplication(id);
        });
        String expectedMessage = "maximum number of TA's was already reached for this course";
        String actualMessage = exception.getMessage();

        Assertions.assertEquals(actualMessage.contains(expectedMessage), true);
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationCreationFault() throws Exception {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        when(applicationService.isTASpotAvailable(courseId)).thenReturn(true);
        when(applicationService.createTA(studentId,courseId)).thenReturn(false);
        Assertions.assertEquals(applicationController.acceptApplication(id), false);
        verify(applicationRepository, never()).save(any(Application.class));
    }

}
