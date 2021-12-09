package nl.tudelft.sem.Application;

import nl.tudelft.sem.Application.controllers.ApplicationController;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.ApplicationService;
import nl.tudelft.sem.Application.services.validator.IsCourseOpen;
import nl.tudelft.sem.Application.services.validator.IsGradeSufficient;
import nl.tudelft.sem.Application.services.validator.IsUniqueApplication;
import nl.tudelft.sem.Application.services.validator.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.management.InvalidApplicationException;
import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ValidatorTests {
    @InjectMocks
    IsCourseOpen isCourseOpen;

    @InjectMocks
    IsGradeSufficient isGradeSufficient;

    @InjectMocks
    IsUniqueApplication isUniqueApplication;

    @Mock
    ApplicationService applicationService;

    @Mock
    ApplicationRepository applicationRepository;


    UUID id;
    UUID courseId;
    UUID studentId;
    Application application;

    @BeforeEach
    public void init() {
        id = UUID.randomUUID();
        courseId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        application = new Application(courseId, studentId);
    }

    @Test
    public void isCourseOpenSuccessfulTest() throws Exception {
        LocalDate startDate = LocalDate.now();
        startDate = startDate.plusWeeks(1);
        when(applicationService.getCourseStartDate(application.getCourseId())).thenReturn(startDate);


        Assertions.assertEquals(isCourseOpen.handle(application) , true);
    }

    @Test
    public void isCourseOpenNoCourseTest() throws Exception {
        LocalDate startDate = LocalDate.now();
        when(applicationService.getCourseStartDate(application.getCourseId())).thenReturn(null);
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            isCourseOpen.handle(application);
        });
        String expectedMessage = "Could not retrieve startDate that was linked to the given courseId";
        String actualMessage = exception.getMessage();

        Assertions.assertEquals(actualMessage.contains(expectedMessage),true);

    }

    @Test
    public void isCourseOpenTooEarlyTest() throws Exception {
        LocalDate startDate = LocalDate.now();
        startDate = startDate.plusWeeks(4);
        when(applicationService.getCourseStartDate(application.getCourseId())).thenReturn(startDate);
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            isCourseOpen.handle(application);
        });
        String expectedMessage = "The course is not yet open to applications";
        String actualMessage = exception.getMessage();

        Assertions.assertEquals(actualMessage.contains(expectedMessage),true);

    }

    @Test
    public void isCourseOpenOnDeadlineTest1() throws Exception {
        LocalDate startDate = LocalDate.now();
        when(applicationService.getCourseStartDate(application.getCourseId())).thenReturn(startDate);

        Assertions.assertEquals(isCourseOpen.handle(application) , true);

    }

    @Test
    public void isCourseOpenOnDeadlineTest2() throws Exception {
        LocalDate startDate = LocalDate.now();
        startDate = startDate.plusWeeks(3);
        when(applicationService.getCourseStartDate(application.getCourseId())).thenReturn(startDate);

        Assertions.assertEquals(isCourseOpen.handle(application) , true);

    }

    @Test
    public void isCourseOpenTooLateTest() throws Exception {
        LocalDate startDate = LocalDate.now();
        startDate = startDate.minusDays(1);

        when(applicationService.getCourseStartDate(application.getCourseId())).thenReturn(startDate);
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            isCourseOpen.handle(application);
        });

        String expectedMessage = "The period for applications has passed";
        String actualMessage = exception.getMessage();

        Assertions.assertEquals(actualMessage.contains(expectedMessage),true);
    }

    @Test
    public void IsGradeSufficientSuccessfulTest() throws Exception {
        when(applicationService.getGrade(studentId, courseId)).thenReturn(9D);

        Assertions.assertEquals(isGradeSufficient.handle(application) , true);
    }

    @Test
    public void IsGradeSufficientGradeTooLowTest() throws Exception {
        when(applicationService.getGrade(studentId, courseId)).thenReturn(5.9);

        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            isGradeSufficient.handle(application);
        });

        String expectedMessage = "Grade was not sufficient";
        String actualMessage = exception.getMessage();

        Assertions.assertEquals(actualMessage.contains(expectedMessage),true);
    }

    @Test
    public void IsGradeSufficientNoGradeFoundTest() throws Exception {
        when(applicationService.getGrade(studentId, courseId)).thenReturn(null);

        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            isGradeSufficient.handle(application);
        });

        String expectedMessage = "could not retrieve course grade with the given student and course IDs";
        String actualMessage = exception.getMessage();

        Assertions.assertEquals(actualMessage.contains(expectedMessage),true);
    }

    @Test
    public void IsApplicationUniqueSuccessfulTest() throws Exception {
        when(applicationRepository.findApplicationByStudentIdAndCourseId(studentId, courseId)).thenReturn(Optional.empty());

        Assertions.assertEquals(isUniqueApplication.handle(application) , true);
    }

    @Test
    public void IsApplicationUniqueFalseTest() throws Exception {
        when(applicationRepository.findApplicationByStudentIdAndCourseId(studentId, courseId)).thenReturn(Optional.ofNullable(application));

        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            isUniqueApplication.handle(application);
        });

        String expectedMessage = "There already exists an application with that student and courseID";
        String actualMessage = exception.getMessage();

        Assertions.assertEquals(actualMessage.contains(expectedMessage),true);
    }

    @Test
    public void ValidatorCheckNextSuccessful() throws Exception {
        Validator validator = isCourseOpen;
        isCourseOpen.setLast(isGradeSufficient);
        isCourseOpen.setLast(isUniqueApplication);
        LocalDate startDate = LocalDate.now();
        startDate = startDate.plusWeeks(1);
        when(applicationService.getCourseStartDate(application.getCourseId())).thenReturn(startDate);
        when(applicationService.getGrade(studentId, courseId)).thenReturn(9D);
        when(applicationRepository.findApplicationByStudentIdAndCourseId(studentId, courseId)).thenReturn(Optional.empty());

        Assertions.assertEquals(isUniqueApplication.handle(application) , true);
    }

    @Test
    public void ValidatorCheckNextFalse() throws Exception {
        Validator validator = isCourseOpen;
        isCourseOpen.setLast(isGradeSufficient);
        isCourseOpen.setLast(isUniqueApplication);
        LocalDate startDate = LocalDate.now();
        startDate = startDate.plusWeeks(1);
        when(applicationService.getCourseStartDate(application.getCourseId())).thenReturn(startDate);
        when(applicationService.getGrade(studentId, courseId)).thenReturn(5.9);
        when(applicationRepository.findApplicationByStudentIdAndCourseId(studentId, courseId)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            validator.handle(application);
        });

        String expectedMessage = "Grade was not sufficient";
        String actualMessage = exception.getMessage();

        Assertions.assertEquals(actualMessage.contains(expectedMessage),true);
    }



}
