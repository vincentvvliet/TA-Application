package nl.tudelft.sem.Application.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import java.io.IOException;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.validator.IsCourseOpen;
import nl.tudelft.sem.Application.services.validator.Validator;
import nl.tudelft.sem.DTO.ApplyingStudentDTO;
import nl.tudelft.sem.DTO.GradeDTO;
import nl.tudelft.sem.DTO.RatingDTO;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
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

    private static final Gson gson = new GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
        .create();

    List<Application> applicationList;
    UUID id;
    UUID courseId;
    UUID studentId;
    Application application;

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

        assertTrue(applicationService.validate(application));
    }

    @Test
    public void validateNotValidTest() throws Exception {
        Exception e = mock(Exception.class);
        when(isCourseOpen.handle(application)).thenThrow(e);

        assertFalse(applicationService.validate(application));
        verify(e).printStackTrace();
    }

    @Test
    public void getApplicationsByCourseTest() {
        when(applicationRepository.findApplicationsByCourseId(courseId)).thenReturn(applicationList);
        assertEquals(applicationService.getApplicationsByCourse(courseId), applicationList);
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

    /**
     * getApplicationDetails tests
     */
    @Test
    void getApplicationDetails_validList_returnsListOfApplyingStudentDTOs() throws Exception{
        // Arrange
        UUID courseId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        Application app1 = new Application(courseId, studentId);
        List<Application> list = List.of(app1);
        // Mock request to Grade MS
        mockBackEnd.enqueue(new MockResponse()
            .setBody(gson.toJson(new GradeDTO(studentId, 8.0d)))
            .addHeader("Content-Type", "application/json"));
        // Mock request to TA MS
        mockBackEnd.enqueue(new MockResponse()
            .setBody(gson.toJson(new RatingDTO(studentId, 4)))
            .addHeader("Content-Type", "application/json"));
        // Act
        List<ApplyingStudentDTO> result = applicationService.getApplicationDetails(list, mockBackEnd.getPort(), mockBackEnd.getPort());
        // Assert
        List<ApplyingStudentDTO> expected = List.of(new ApplyingStudentDTO(studentId, 8.0d, Optional.of(4)));
        assertEquals(expected, result);

    }
    @Test
    void getApplicationDetails_notAvailable_throwsException() {
        // Arrange
        UUID courseId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        Application app1 = new Application(courseId, studentId);
        List<Application> list = List.of(app1);
        // Mock request to Grade MS
        mockBackEnd.enqueue(new MockResponse()
            .setResponseCode(404));
        // Act & Assert
        Exception e = assertThrows(Exception.class,
            () -> applicationService.getApplicationDetails(
                list, mockBackEnd.getPort(), mockBackEnd.getPort()
            ));
    }


    /**
     * getGradeByCourseIdAndStudentId tests
     */
    @Test
    void getGradeByCourseIdAndStudentId_succes_returnGradeDTO() {
        // Arrange
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        GradeDTO grade = new GradeDTO(studentId, 9.0d);
        mockBackEnd.enqueue(new MockResponse()
            .setBody(gson.toJson(grade)).addHeader("Content-Type", "application/json"));
        // Act
        GradeDTO result = null;
        try {
            result = applicationService.getGradeByCourseIdAndStudentId(courseId, studentId, mockBackEnd.getPort());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // Assert
        assertEquals(grade, result);
    }

    @Test
    void getGradeByCourseIdAndStudentId_failure_exception_thrown() {
        // Arrange
        mockBackEnd.enqueue(new MockResponse()
            .setBody(gson.toJson(null)).addHeader("Content-Type", "application/json"));
        // Act
        Exception e = assertThrows(Exception.class,
            () -> applicationService.getGradeByCourseIdAndStudentId(courseId, studentId, mockBackEnd.getPort())
        );
        // Assert
        assertEquals("No grade found!", e.getMessage());
    }

    /**
     * getRatingForTA tests
     */
    @Test
    void getRatingForTA_gradeFound_returnsRatingDTO() {
        // Arrange
        UUID studentId = UUID.randomUUID();
        RatingDTO rating = new RatingDTO(studentId, 4);
        mockBackEnd.enqueue(new MockResponse()
            .setBody(gson.toJson(rating)).addHeader("Content-Type", "application/json"));
        // Act
        RatingDTO result = null;
        try {
            result = applicationService.getRatingForTA(studentId, mockBackEnd.getPort());
        } catch (Exception e) {
            fail(e.getMessage());
        }
        // Assert
        assertEquals(rating, result);
    }

    @Test
    void getRatingForTA_gradeNotFound_throwsException() {
        // Arrange
        mockBackEnd.enqueue(new MockResponse()
            .setBody(gson.toJson(null)).addHeader("Content-Type", "application/json"));
        // Act
        Exception e = assertThrows(Exception.class,
            () -> applicationService.getRatingForTA(courseId, mockBackEnd.getPort())
        );
        // Assert
        assertEquals("No TA rating found!", e.getMessage());
    }
}
