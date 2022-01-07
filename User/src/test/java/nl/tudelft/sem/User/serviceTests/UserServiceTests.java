package nl.tudelft.sem.User.serviceTests;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import nl.tudelft.sem.User.services.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
public class UserServiceTests {
// this is a draft for now since we don't have a definitive way of testing webservers yet
    @Autowired
    UserService userService;

    private UUID studentId;
    private UUID courseId;


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
    public void init(){
        studentId = UUID.randomUUID();
        courseId = UUID.randomUUID();
    }

    @Test
    void removeApplicationSuccess() throws Exception {
        Boolean expected  = true;

        mockBackEnd.enqueue(new MockResponse()
                .setBody(expected.toString()).addHeader("Content-Type", "application/json"));

        boolean booleanAccepted = userService.removeApplication(studentId, courseId, mockBackEnd.getPort());
        Assertions.assertEquals(booleanAccepted, expected);
    }

    @Test
    void removeApplicationNoSuccess() throws Exception {
        Boolean expected  = false;

        mockBackEnd.enqueue(new MockResponse()
                .setBody(expected.toString()).addHeader("Content-Type", "application/json"));

        boolean booleanAccepted = userService.removeApplication(studentId, courseId, mockBackEnd.getPort());
        Assertions.assertEquals(booleanAccepted, expected);
    }
}
