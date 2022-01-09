package nl.tudelft.sem.TAs.serviceTests;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import nl.tudelft.sem.TAs.entities.Contract;
import nl.tudelft.sem.TAs.repositories.ContractRepository;
import nl.tudelft.sem.TAs.services.ContractService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class ContractServiceTest {

    @Autowired
    ContractService contractService;

    @MockBean
    ContractRepository contractRepository;

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
    void sendContractNotificationSuccessful() throws Exception {
        Boolean expected  = true;
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        Contract contract = new Contract(studentId, courseId);
        when(contractRepository.findByStudentIdAndCourseId(studentId, courseId)).thenReturn(Optional.of(contract));
        mockBackEnd.enqueue(new MockResponse()
                .setBody(expected.toString()).addHeader("Content-Type", "application/json"));
        boolean booleanAccepted = contractService.sendContractNotification(studentId, courseId, mockBackEnd.getPort());
        Assertions.assertTrue(booleanAccepted);
    }
}
