package nl.tudelft.sem.Application.services;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

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
    void createTA_successful() throws Exception {
        Boolean expected  = true;
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(expected.toString()).addHeader("Content-Type", "application/json"));

        boolean booleanAccepted = applicationService.createTA(studentId, courseId, mockBackEnd.getPort());
        Assertions.assertEquals(booleanAccepted, expected);
    }
}
