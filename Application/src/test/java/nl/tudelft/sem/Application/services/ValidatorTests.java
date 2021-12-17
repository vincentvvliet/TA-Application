package nl.tudelft.sem.Application.services;

import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
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
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
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
    LocalDate deadline;

    @BeforeEach
    public void init() {
        id = UUID.randomUUID();
        courseId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        application = new Application(courseId, studentId);
        deadline = LocalDate.now().plusWeeks(3);
    }

    @Test
    public void isCourseOpenSuccessfulTest() throws Exception {
        deadline = deadline.plusWeeks(1);
        when(applicationService.getCourseStartDate(application.getCourseId())).thenReturn(deadline);


        Assertions.assertEquals(isCourseOpen.handle(application) , true);
    }

    @Test
    public void isCourseOpenNoCourseTest() throws Exception {
        when(applicationService.getCourseStartDate(application.getCourseId())).thenReturn(null);
        Exception exception = Assertions.assertThrows(Exception.class, () -> isCourseOpen.handle(application));
        String expectedMessage = "Could not retrieve startDate that was linked to the given courseId";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    public void isCourseOpenNullTest() {
        application = new Application(null, studentId);
        Exception exception = Assertions.assertThrows(Exception.class, () -> isCourseOpen.handle(application));
        String expectedMessage = "The given application does not contain a course ID";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    public void isCourseOpenOnDeadlineTest() throws Exception {
        when(applicationService.getCourseStartDate(application.getCourseId())).thenReturn(deadline);

        Assertions.assertEquals(isCourseOpen.handle(application) , true);

    }

    @Test
    public void isCourseOpenTooLateTest() throws Exception {
        deadline = deadline.minusDays(1);

        when(applicationService.getCourseStartDate(application.getCourseId())).thenReturn(deadline);
        Exception exception = Assertions.assertThrows(Exception.class, () -> isCourseOpen.handle(application));

        String expectedMessage = "The period for applications has passed";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void isCourseOpenTooLateTest2() throws Exception {
        deadline = deadline.minusWeeks(1);

        when(applicationService.getCourseStartDate(application.getCourseId())).thenReturn(deadline);
        Exception exception = Assertions.assertThrows(Exception.class, () -> isCourseOpen.handle(application));

        String expectedMessage = "The period for applications has passed";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void IsGradeSufficientSuccessfulTest() throws Exception {
        when(applicationService.getGrade(studentId, courseId)).thenReturn(9D);

        Assertions.assertEquals(isGradeSufficient.handle(application) , true);
    }

    @Test
    public void IsGradeSufficientGradeTooLowTest() throws Exception {
        when(applicationService.getGrade(studentId, courseId)).thenReturn(5.9);

        Exception exception = Assertions.assertThrows(Exception.class, () -> isGradeSufficient.handle(application));

        String expectedMessage = "Grade was not sufficient";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void IsGradeSufficientNoGradeFoundTest() throws Exception {
        when(applicationService.getGrade(studentId, courseId)).thenReturn(null);

        Exception exception = Assertions.assertThrows(Exception.class, () -> isGradeSufficient.handle(application));

        String expectedMessage = "could not retrieve course grade with the given student and course IDs";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void IsApplicationUniqueSuccessfulTest() throws Exception {
        when(applicationRepository.findByStudentIdAndCourseId(studentId, courseId)).thenReturn(Optional.empty());

        Assertions.assertEquals(isUniqueApplication.handle(application) , true);
    }

    @Test
    public void IsApplicationUniqueFalseTest() {
        when(applicationRepository.findByStudentIdAndCourseId(studentId, courseId)).thenReturn(Optional.ofNullable(application));

        Exception exception = Assertions.assertThrows(Exception.class, () -> isUniqueApplication.handle(application));

        String expectedMessage = "There already exists an application with that student and courseID";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void ValidatorCheckNextSuccessful() throws Exception {
        Validator validator = isCourseOpen;
        isCourseOpen.setLast(isGradeSufficient);
        isCourseOpen.setLast(isUniqueApplication);
        deadline = deadline.plusWeeks(1);
        when(applicationService.getCourseStartDate(application.getCourseId())).thenReturn(deadline);
        when(applicationService.getGrade(studentId, courseId)).thenReturn(9D);
        when(applicationRepository.findByStudentIdAndCourseId(studentId, courseId)).thenReturn(Optional.empty());

        Assertions.assertEquals(validator.handle(application) , true);
    }

    @Test
    public void ValidatorCheckNextFalse() throws Exception {
        Validator validator = isCourseOpen;
        isCourseOpen.setLast(isGradeSufficient);
        isCourseOpen.setLast(isUniqueApplication);
        deadline = deadline.plusWeeks(1);
        when(applicationService.getCourseStartDate(application.getCourseId())).thenReturn(deadline);
        when(applicationService.getGrade(studentId, courseId)).thenReturn(5.9);
        when(applicationRepository.findByStudentIdAndCourseId(studentId, courseId)).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(Exception.class, () -> validator.handle(application));

        String expectedMessage = "Grade was not sufficient";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }





}
