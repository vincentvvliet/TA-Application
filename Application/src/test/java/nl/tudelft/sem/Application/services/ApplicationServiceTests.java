package nl.tudelft.sem.Application.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import java.io.IOException;
import java.time.LocalDate;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.exceptions.EmptyResourceException;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.validator.IsCourseOpen;
import nl.tudelft.sem.Application.services.validator.Validator;
import nl.tudelft.sem.DTO.ApplyingStudentDTO;
import nl.tudelft.sem.DTO.GradeDTO;
import nl.tudelft.sem.DTO.RatingDTO;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Assertions;
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
    LocalDate startDateClosed;
    LocalDate startDateOpen;
    ApplicationService applicationServiceSpy;

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
        startDateClosed = LocalDate.now().plusWeeks(2);
        startDateOpen = LocalDate.now().plusWeeks(4);
        applicationServiceSpy = spy(applicationService);
    }

    /**
     * Validation tests
     */
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

    /**
     * createTA tests
     */
    @Test
    public void createTA_succes_returnsTrue() throws Exception {
        // Arrange
        mockBackEnd.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody(gson.toJson(true))
        );
        // Act
        boolean result = applicationService.createTA(studentId, courseId, mockBackEnd.getPort());
        // Assert
        assertTrue(result);
    }

    @Test
    public void createTA_noSucces_throwsException() throws Exception {
        // Arrange
        mockBackEnd.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody(gson.toJson(false))
        );
        // Act
        Exception exception = assertThrows(Exception.class,
            () -> applicationService.createTA(studentId, courseId, mockBackEnd.getPort()));
        // Assert
        assertEquals("Could not create TA.", exception.getMessage());
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
     * getCourseStartDate tests
     */
//    @Test
//    void getCourseStartDate_test() throws EmptyResourceException {
//        // Arrange
//        LocalDate date = LocalDate.now();
//        mockBackEnd.enqueue(new MockResponse()
//            .addHeader("Content-Type", "application/json")
//            .setBody(gson.toJson(date))
//        );
//        // Act
//        LocalDate result = applicationService.getCourseStartDate(courseId, mockBackEnd.getPort());
//        // Assert
//        assertEquals(date, result);
//    }

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
            .setBody(gson.toJson(null))
            .addHeader("Content-Type", "application/json"));
        // Act
        Exception e = assertThrows(Exception.class,
            () -> applicationService.getRatingForTA(courseId, mockBackEnd.getPort())
        );
        // Assert
        assertEquals("No TA rating found!", e.getMessage());
    }

    /**
     * getGradesByCourseId -> To Be Removed!
     */

    /**
     * getOverlappingCourses tests
     */
    @Test
    void getOverlappingCourses_empty_returnsList() {
        // Arrange
        List<UUID> expected = List.of();
        mockBackEnd.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody(gson.toJson(expected))
        );
        // Act
        List<UUID> result = applicationService.getOverlappingCourses(courseId, mockBackEnd.getPort());
        // Assert
        assertEquals(expected, result);
    }

    @Test
    void getOverlappingCourses_valid_returnsList() {
        // Arrange
        List<UUID> expected = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        mockBackEnd.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody(gson.toJson(expected))
        );
        // Act
        List<UUID> result = applicationService.getOverlappingCourses(courseId, mockBackEnd.getPort());
        // Assert
        assertEquals(expected, result);
    }

    /**
     * studentCanTAAnotherCourse tests
     */
    @Test
    void studentCanTAAnotherCourse_positive_returnTrue() {
        // Arrange
        List<UUID> overlap = List.of(UUID.randomUUID(), UUID.randomUUID());
        when(applicationRepository.coursesAcceptedAsTA(studentId))
            .thenReturn(overlap);
        mockBackEnd.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody(gson.toJson(overlap))
        );
        // Act
        boolean result = applicationService.studentCanTAAnotherCourse(studentId, courseId, mockBackEnd.getPort());
        // Assert
        assertTrue(result);
    }

    @Test
    void studentCanTAAnotherCourse_negative_returnsFalse() {
        // Arrange
        List<UUID> overlap = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        when(applicationRepository.coursesAcceptedAsTA(studentId))
            .thenReturn(overlap);
        mockBackEnd.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody(gson.toJson(overlap))
        );
        // Act
        boolean result = applicationService.studentCanTAAnotherCourse(studentId, courseId,
            mockBackEnd.getPort());
        // Assert
        assertFalse(result);
    }

    /**
     * sendNotification tests
     */
    @Test
    void sendNotifications_succes_returnsTrue() throws Exception {
        // Arrange
        mockBackEnd.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody(gson.toJson(true))
        );
        // Act
        boolean result = applicationService.sendNotification(
            UUID.randomUUID(),
            "You've gotten a contract!",
            mockBackEnd.getPort());
        // Assert
        assertTrue(result);
    }

    @Test
    void sendNotifications_noSucces_throwsException() {
        // Arrange
        mockBackEnd.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody(gson.toJson(false))
        );
        // Act
        Exception exception = assertThrows(Exception.class, () -> applicationService.sendNotification(
            UUID.randomUUID(),"You've gotten a contract!", mockBackEnd.getPort())
        );
        // Assert
        assertEquals("Could not create notification for user.", exception.getMessage());
    }

    /**
     * sendContract
     */
    @Test
    void sendContract_succes_returnsContract() throws Exception {
        // Arrange
        mockBackEnd.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody(gson.toJson("This is a contract, do you accept?"))
        );
        // Act
        String result = applicationService.sendContract(
            studentId,
            courseId,
            mockBackEnd.getPort());
        // Assert
        assertEquals("\"This is a contract, do you accept?\"", result);
    }

    @Test
    void sendContract_responseEmpty_throwsEmptyResourceException() {
        // Arrange
        mockBackEnd.enqueue(new MockResponse());
        // Act
        Exception exception = assertThrows(Exception.class, () -> applicationService.sendContract(
            studentId, courseId, mockBackEnd.getPort()
        ));
        // Assert
        assertEquals("Could not send contract to User", exception.getMessage());
    }

    /**
     * createContract
     */
    @Test
    void createContract_succes_returnsId() throws EmptyResourceException {
        // Arrange
        UUID id = UUID.randomUUID();
        mockBackEnd.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody(gson.toJson(id))
        );
        // Act
        UUID result = applicationService.createContract(
            studentId,
            courseId,
            mockBackEnd.getPort());
        // Assert
        assertEquals(id, result);
    }

    @Test
    void createContract_responseIsEmpty_throwsEmptyResourceException() {
        // Arrange
        mockBackEnd.enqueue(new MockResponse());
        // Act
        Exception exception = assertThrows(Exception.class, () -> applicationService.createContract(
            studentId, courseId, mockBackEnd.getPort()
        ));
        // Assert
        assertEquals("Contract creation failed", exception.getMessage());
    }

    /**
     * addContract
     */
    @Test
    void addContract_succes_returnsId() throws EmptyResourceException {
        // Arrange
        mockBackEnd.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody(gson.toJson(true))
        );
        // Act
        boolean result = applicationService.addContract(
            studentId,
            courseId,
            mockBackEnd.getPort());
        // Assert
        assertTrue(result);
    }

    @Test
    void addContract_noSucces_throwsEmptyResourceException() {
        // Arrange
        mockBackEnd.enqueue(new MockResponse()
            .addHeader("Content-Type", "application/json")
            .setBody(gson.toJson(false))
        );
        // Act
        Exception result = assertThrows(Exception.class, () ->
            applicationService.addContract(studentId, courseId, mockBackEnd.getPort())
        );
        // Assert
        assertEquals("Could not link contract to TA", result.getMessage());
    }

    @Test
    void addContract_emptyResponse_throwsEmptyResourceException() {
        // Arrange
        mockBackEnd.enqueue(new MockResponse());
        // Act
        Exception result = assertThrows(Exception.class, () ->
            applicationService.addContract(studentId, courseId, mockBackEnd.getPort())
        );
        // Assert
        assertEquals("Could not link contract to TA", result.getMessage());
    }

    /**
     * acceptApplication tests
     */
    @Test
    void acceptApplicationCourseStillOpen() throws Exception {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        doReturn(true).when(applicationServiceSpy).isTASpotAvailable(courseId, 47112);
        doReturn(true).when(applicationServiceSpy).createTA(studentId,courseId, 47110);
        doReturn(startDateOpen).when(applicationServiceSpy).getCourseStartDate(courseId, 47112);
        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationServiceSpy.acceptApplication(id));
        String expectedMessage = "application is still open for application";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    void acceptApplicationCourseHasStarted() throws Exception {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        doReturn(true).when(applicationServiceSpy).isTASpotAvailable(courseId, 47110);
        doReturn(true).when(applicationServiceSpy).studentCanTAAnotherCourse(studentId, courseId, 47112);
        doReturn(true).when(applicationServiceSpy).createTA(studentId,courseId, 47110);
        doReturn(LocalDate.now().minusWeeks(1)).when(applicationServiceSpy).getCourseStartDate(courseId, 47112);
        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationServiceSpy.acceptApplication(id));
        String expectedMessage = "course has already started";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationSuccessfully() throws Exception {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        doReturn(true).when(applicationServiceSpy).studentCanTAAnotherCourse(studentId,courseId, 47112);
        doReturn(true).when(applicationServiceSpy).isTASpotAvailable(courseId, 47112);
        doReturn(true).when(applicationServiceSpy).studentCanTAAnotherCourse(studentId, courseId, 47112);
        doReturn(true).when(applicationServiceSpy).createTA(studentId,courseId, 47110);
        doReturn(startDateClosed).when(applicationServiceSpy).getCourseStartDate(courseId,47112);
        doReturn(id).when(applicationServiceSpy).createContract(studentId,courseId,47110);
        doReturn(true).when(applicationServiceSpy).addContract(studentId,id,47110);
        doReturn(true).when(applicationServiceSpy).sendNotification(studentId,"You have been accepted for a TA position, you can expect a contract shortly.",47111);
        Assertions.assertEquals(applicationServiceSpy.acceptApplication(id).block(), true);
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    public void acceptApplicationAlreadyAccepted() {
        application.setAccepted(true);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        Exception exception = assertThrows(Exception.class, () -> applicationService.acceptApplication(id));
        String expectedMessage = "application is already accepted";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationNoApplicationInRepository() {
        when(applicationRepository.findById(id)).thenReturn(Optional.empty());
        Exception exception = assertThrows(NoSuchElementException.class, () -> applicationService.acceptApplication(id));
        String expectedMessage = "application does not exist";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationMaximumTAsReached() throws EmptyResourceException {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        doReturn(true).when(applicationServiceSpy).studentCanTAAnotherCourse(studentId, courseId, 47112);
        doReturn(false).when(applicationServiceSpy).isTASpotAvailable(courseId, 47112);
        doReturn(startDateClosed).when(applicationServiceSpy).getCourseStartDate(courseId, 47112);

        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationServiceSpy.acceptApplication(id));
        String expectedMessage = "maximum number of TA's was already reached for this course";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationAlreadyTAFor3CoursesPerQuarter() throws EmptyResourceException {
        application.setAccepted(false);
        doReturn(Optional.ofNullable(application)).when(applicationRepository).findById(id);
        doReturn(false).when(applicationServiceSpy).studentCanTAAnotherCourse(studentId, courseId, 47112);
        doReturn(startDateClosed).when(applicationServiceSpy).getCourseStartDate(courseId, 47112);
        Exception exception = Assertions.assertThrows(Exception.class, () -> applicationServiceSpy.acceptApplication(id));
        String expectedMessage = "a student can TA a maximum of 3 courses per quarter";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }

    @Test
    public void acceptApplicationCreationFault() throws Exception {
        application.setAccepted(false);
        when(applicationRepository.findById(id)).thenReturn(Optional.ofNullable(application));
        doReturn(true).when(applicationServiceSpy).isTASpotAvailable(courseId, 47110);
        doReturn(true).when(applicationServiceSpy).studentCanTAAnotherCourse(studentId, courseId, 47112);
        doReturn(startDateClosed).when(applicationServiceSpy).getCourseStartDate(courseId, 47112);
        doThrow(new Exception("Could not create TA.")).when(applicationServiceSpy).createTA(studentId, courseId, 47110);
        doReturn(true).when(applicationServiceSpy).isTASpotAvailable(courseId, 47112);
        Exception thrown =
                Assertions.assertThrows(Exception.class, () -> {
                    applicationServiceSpy.acceptApplication(id);
                });
        String expectedMessage = "Could not create TA.";
        String actualMessage = thrown.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationRepository, never()).save(any(Application.class));
    }


}
