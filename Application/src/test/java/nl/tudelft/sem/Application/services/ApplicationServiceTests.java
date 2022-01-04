package nl.tudelft.sem.Application.services;

import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.validator.IsCourseOpen;
import nl.tudelft.sem.Application.services.validator.Validator;
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
import reactor.core.publisher.Mono;

import java.util.*;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class ApplicationServiceTests {

    @Autowired
    ApplicationService applicationService;

    @MockBean
    ApplicationRepository applicationRepository;

    @MockBean
    IsCourseOpen isCourseOpen;

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
        doNothing().when(isCourseOpen).setLast(any(Validator.class));
    }

    @Test
    public void validateSuccessfulTest() throws Exception {
        when(isCourseOpen.handle(application)).thenReturn(true);

        Assertions.assertTrue(applicationService.validate(application));
    }

    @Test
    public void validateNotValidTest() throws Exception {
        Exception e = mock(Exception.class);
        when(isCourseOpen.handle(application)).thenThrow(e);

        Assertions.assertFalse(applicationService.validate(application));
        verify(e).printStackTrace();
    }

    @Test
    public void getApplicationsByCourseTest() {
        when(applicationRepository.findApplicationsByCourseId(courseId)).thenReturn(applicationList);
        Assertions.assertEquals(applicationService.getApplicationsByCourse(courseId), applicationList);
    }

    @Test
    public void removeApplicationSuccessfulTest() throws Exception {
        when(applicationRepository.findByStudentIdAndCourseId(studentId,courseId)).thenReturn(Optional.ofNullable(application));
        when(isCourseOpen.handle(application)).thenReturn(true);

        Assertions.assertTrue(applicationService.removeApplication(studentId,courseId));

        verify(applicationRepository).deleteApplicationByStudentIdAndCourseId(studentId,courseId);

    }

    @Test
    public void removeApplicationNoApplicationInRepositoryTest() throws Exception {
        when(applicationRepository.findByStudentIdAndCourseId(studentId,courseId)).thenReturn(Optional.ofNullable(null));

        Assertions.assertFalse(applicationService.removeApplication(studentId,courseId));
        verify(applicationRepository, never()).deleteApplicationByStudentIdAndCourseId(studentId,courseId);

    }

    @Test
    public void removeApplicationCourseNotOpenTest() throws Exception {
        when(applicationRepository.findByStudentIdAndCourseId(studentId,courseId)).thenReturn(Optional.of(application));
        when(isCourseOpen.handle(application)).thenThrow(new Exception());

        Assertions.assertFalse(applicationService.removeApplication(studentId,courseId));
        verify(applicationRepository, never()).deleteApplicationByStudentIdAndCourseId(studentId,courseId);
    }

}
