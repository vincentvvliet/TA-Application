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
public class ApplicationServiceTests {

    @InjectMocks
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

}
