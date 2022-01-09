package nl.tudelft.sem.Application.services;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class ApplicationServiceMockWebServerTests {

    @Autowired
    ApplicationService applicationService;
    @MockBean
    ApplicationRepository applicationRepository;

    public static MockWebServer mockBackEnd;

    // start up the Mock Web Server
    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    // shut down the Mock Web Server
    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void createTA_successfulCreation() {
        Boolean expected  = true;
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(expected.toString()).addHeader("Content-Type", "application/json"));

        boolean booleanAccepted = applicationService.createTA(studentId, courseId, mockBackEnd.getPort());
        Assertions.assertTrue(booleanAccepted);
    }

    @Test
    void createTA_failedCreation() {
        Boolean expected  = false;
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(expected.toString()).addHeader("Content-Type", "application/json"));

        boolean booleanAccepted = applicationService.createTA(studentId, courseId, mockBackEnd.getPort());
        Assertions.assertFalse(booleanAccepted);
    }

    @Test
    void createTA_emptyResponse() {
        Boolean expected = null;
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(expected + "").addHeader("Content-Type", "application/json"));

        boolean booleanAccepted = applicationService.createTA(studentId, courseId, mockBackEnd.getPort());
        Assertions.assertFalse(booleanAccepted);
    }

    /**
     * 40 students and 1 TA selected -> 1 spot available
     */
    @Test
    void TASpotAvailable_yes() {
        Integer enrolledStudents = 40;
        UUID courseId = UUID.randomUUID();
        Mockito.when(applicationRepository.numberSelectedTAsForCourse(courseId)).thenReturn(1);

        mockBackEnd.enqueue(new MockResponse()
                .setBody(enrolledStudents.toString()).addHeader("Content-Type", "application/json"));

        boolean booleanAvailable = applicationService.isTASpotAvailable(courseId, mockBackEnd.getPort());
        Assertions.assertTrue(booleanAvailable);
    }

    /**
     * 40 students and 2 TAs selected -> no spot available
     */
    @Test
    void TASpotAvailable_no_fullNumber() {
        Integer enrolledStudents = 40;
        UUID courseId = UUID.randomUUID();
        Mockito.when(applicationRepository.numberSelectedTAsForCourse(courseId)).thenReturn(2);

        mockBackEnd.enqueue(new MockResponse()
                .setBody(enrolledStudents.toString()).addHeader("Content-Type", "application/json"));

        boolean booleanAvailable = applicationService.isTASpotAvailable(courseId, mockBackEnd.getPort());
        Assertions.assertFalse(booleanAvailable);
    }

    /**
     * 27 students and 1 TA selected -> no spot available
     */
    @Test
    void TASpotAvailable_no_intermediateNumber() {
        Integer enrolledStudents = 27;
        UUID courseId = UUID.randomUUID();
        Mockito.when(applicationRepository.numberSelectedTAsForCourse(courseId)).thenReturn(1);

        mockBackEnd.enqueue(new MockResponse()
                .setBody(enrolledStudents.toString()).addHeader("Content-Type", "application/json"));

        boolean booleanAvailable = applicationService.isTASpotAvailable(courseId, mockBackEnd.getPort());
        Assertions.assertFalse(booleanAvailable);
    }

    /**
     * empty response (-> 0 students) students -> no spot available
     */
    @Test
    void TASpotAvailable_no_emptyResponse() {
        UUID courseId = UUID.randomUUID();
        Mockito.when(applicationRepository.numberSelectedTAsForCourse(courseId)).thenReturn(1);

        mockBackEnd.enqueue(new MockResponse()
                .setBody(null + "").addHeader("Content-Type", "application/json"));

        boolean booleanAvailable = applicationService.isTASpotAvailable(courseId, mockBackEnd.getPort());
        Assertions.assertFalse(booleanAvailable);
    }
}
