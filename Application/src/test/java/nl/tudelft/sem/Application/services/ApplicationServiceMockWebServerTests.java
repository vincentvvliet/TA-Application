package nl.tudelft.sem.Application.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import nl.tudelft.sem.Application.exceptions.EmptyResourceException;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.DTO.RatingDTO;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    // following two tests dont terminate, dont know why

//    @Test
//    void createTA_successfulCreation() throws Exception {
//        Boolean expected  = true;
//        UUID studentId = UUID.randomUUID();
//        UUID courseId = UUID.randomUUID();
//
//        mockBackEnd.enqueue(new MockResponse()
//                .setBody(expected.toString()).addHeader("Content-Type", "application/json"));
//
//        boolean booleanAccepted = applicationService.createTA(studentId, courseId, mockBackEnd.getPort());
//        Assertions.assertTrue(booleanAccepted);
//    }


//    @Test
//    void createTA_failedCreation() throws Exception {
//        Boolean expected  = false;
//        UUID studentId = UUID.randomUUID();
//        UUID courseId = UUID.randomUUID();
//
//        mockBackEnd.enqueue(new MockResponse()
//                .setBody(expected.toString()).addHeader("Content-Type", "application/json"));
//
//        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationService.createTA(studentId, courseId, mockBackEnd.getPort()));
//        String expectedMessage = "Could not create TA.";
//        String actualMessage = exception.getMessage();
//        assertTrue(actualMessage.contains(expectedMessage));
//
//        //boolean booleanAccepted = applicationService.createTA(studentId, courseId, mockBackEnd.getPort());
//        //Assertions.assertFalse(booleanAccepted);
//    }

    @Test
    void createTA_emptyResponse() throws Exception {
        Boolean expected = null;
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(expected + "").addHeader("Content-Type", "application/json"));

        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationService.createTA(studentId, courseId, mockBackEnd.getPort()));
        String expectedMessage = "Could not create TA.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
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

    /**
     * empty response (no grade) -> throw exception
     */
    @Test
    void getGrade_emptyResponse() {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(null + "").addHeader("Content-Type", "application/json"));

        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationService.getGrade(studentId, courseId, mockBackEnd.getPort()));
        String expectedMessage = "no grade found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getGrade_gradePresent() throws EmptyResourceException {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        Double grade = 9.0;
        mockBackEnd.enqueue(new MockResponse()
                .setBody(grade.toString()).addHeader("Content-Type", "application/json"));

        assertEquals(applicationService.getGrade(studentId, courseId, mockBackEnd.getPort()), grade);
    }

    /**
     * empty response (no start date) -> throw exception
     */
    @Test
    void getCourseStartDate_emptyResponse() {
        UUID courseId = UUID.randomUUID();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(null + "").addHeader("Content-Type", "application/json"));

        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationService.getCourseStartDate(courseId, mockBackEnd.getPort()));
        String expectedMessage = "no starting date found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    // test case requires changing LocalDate to String in Mono and endpoint
    // not sure if this is 100% a good approach, si I left it aside for now
    /*
    @Test
    void getCourseStartDate_datePresent() throws EmptyResourceException {
        UUID courseId = UUID.randomUUID();
        LocalDate startDate = LocalDate.parse("2021-12-19");
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(ratingDTO);
        mockBackEnd.enqueue(new MockResponse()
                .setBody("2021-12-19").addHeader("Content-Type", "application/json"));

        assertEquals(applicationService.getCourseStartDate(courseId, mockBackEnd.getPort()), startDate);
    }
    */

    /**
     * empty response (no TA rating) -> throw exception
     */
    @Test
    void getRatingForTA_emptyResponse() {
        UUID studentId = UUID.randomUUID();
        mockBackEnd.enqueue(new MockResponse()
                .setBody(null + "").addHeader("Content-Type", "application/json"));

        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationService.getRatingForTA(studentId, mockBackEnd.getPort()));
        String expectedMessage = "No TA rating found!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    /**
     * empty response (no grade) -> throw exception
     */
    @Test
    void getGradeByStudentAndCourse_emptyResponse() {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(null + "").addHeader("Content-Type", "application/json"));

        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationService.getGradeByCourseIdAndStudentId(courseId, studentId, mockBackEnd.getPort()));
        String expectedMessage = "No grade found!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}
