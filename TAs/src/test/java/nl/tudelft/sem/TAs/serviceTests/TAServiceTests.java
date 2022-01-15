package nl.tudelft.sem.TAs.serviceTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import java.util.*;
import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.TAs.entities.Contract;
import nl.tudelft.sem.TAs.entities.TA;
import nl.tudelft.sem.TAs.repositories.ContractRepository;
import nl.tudelft.sem.TAs.repositories.TARepository;
import nl.tudelft.sem.TAs.services.TAService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TAServiceTests {

    @Autowired
    TAService taService;

    @MockBean
    TARepository taRepository;

    @MockBean
    ContractRepository contractRepository;

    public static MockWebServer mockBackEnd;
    public static ObjectMapper mapper;

    // start up the Mock Web Server
    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // shut down the Mock Web Server
    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void noJobsYet_isNull() {
        // Arrange
        UUID student = UUID.randomUUID();
        Mockito.when(taRepository.getAverageRating(student)).thenReturn(Optional.empty());
        // Act
        Mono<RatingDTO> mono = taService.getAverageRating(student);
        // Assert
        RatingDTO result = mono.block();
        Assertions.assertNull(result);
    }

    @Test
    void testSingleJob_returnsCorrectValue() {
        // Arrange
        UUID student = UUID.randomUUID();
        int ratingTA = 4;
        Mockito.when(taRepository.getAverageRating(student)).thenReturn(Optional.of(ratingTA));
        // Act
        Mono<RatingDTO> mono = taService.getAverageRating(student);
        // Assert
        RatingDTO result = Objects.requireNonNull(mono.block());
        assertEquals(result.getStudentId(), student);
        assertEquals(result.getRating(), ratingTA);
    }

    @Test
    void testAverageOfThreeJobs_returnsAverageValue() {
        // Arrange
        UUID student = UUID.randomUUID();
        int averageRatingTA = (4 + 5 + 5) / 3;
        Mockito.when(taRepository.getAverageRating(student)).thenReturn(Optional.of(averageRatingTA));
        // Act
        Mono<RatingDTO> mono = taService.getAverageRating(student);
        // Assert
        RatingDTO result = Objects.requireNonNull(mono.block());
        assertEquals(result.getStudentId(), student);
        assertEquals(result.getRating(), averageRatingTA);
    }

    @Test
    void addContract_successful() {
        UUID idContract = UUID.randomUUID();
        Contract contract = new Contract();
        TA ta = new TA();
        when(contractRepository.findById(idContract)).thenReturn(Optional.of(contract));
        assertEquals(true, taService.addContract(Optional.of(ta), idContract).block());
    }

    @Test
    void addContract_emptyTA() {
        UUID idContract = UUID.randomUUID();
        Contract contract = new Contract();
        when(contractRepository.findById(idContract)).thenReturn(Optional.of(contract));
        assertEquals(false, taService.addContract(Optional.empty(), idContract).block());
    }

    @Test
    void addContract_emptyContract() {
        UUID idContract = UUID.randomUUID();
        TA ta = new TA();
        when(contractRepository.findById(idContract)).thenReturn(Optional.empty());
        assertEquals(false, taService.addContract(Optional.of(ta), idContract).block());
    }
}
