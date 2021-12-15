package nl.tudelft.sem.Application.services;

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

import java.util.*;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ApplicationServiceTests {

    @InjectMocks
    ApplicationService applicationService;

    @Mock
    ApplicationRepository applicationRepository;

    @Mock
    IsCourseOpen validator;

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
        doNothing().when(validator).setLast(any(Validator.class));
    }

    @Test
    public void validateSuccessfulTest() throws Exception {
        when(validator.handle(application)).thenReturn(true);

        Assertions.assertEquals(applicationService.validate(application), true);
    }

    @Test
    public void validateNotValidTest() throws Exception {
        Exception e = mock(Exception.class);
        when(validator.handle(application)).thenThrow(e);

        Assertions.assertEquals(applicationService.validate(application), false);
        verify(e).printStackTrace();
    }

    @Test
    public void getApplicationsByCourseTest() throws Exception {
        when(applicationRepository.findAllApplicationsByCourseId(courseId)).thenReturn(applicationList);

        Assertions.assertEquals(applicationService.getApplicationsByCourse(courseId), applicationList);
    }

}
