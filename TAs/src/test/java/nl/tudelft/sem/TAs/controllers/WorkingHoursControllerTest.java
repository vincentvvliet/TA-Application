package nl.tudelft.sem.TAs.controllers;

import nl.tudelft.sem.TAs.entities.WorkingHour;
import nl.tudelft.sem.TAs.repositories.TARepository;
import nl.tudelft.sem.TAs.repositories.WorkingHoursRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class WorkingHoursControllerTest {

    @Autowired
    WorkingHoursController workingHoursController;

    @MockBean
    WorkingHoursRepository workingHoursRepository;

    @MockBean
    TARepository taRepository;

    UUID TAid;
    UUID workingHourId;

    @BeforeEach
    void setup() {
        TAid = UUID.randomUUID();
        workingHourId = UUID.randomUUID();
    }

    @Test
    public void declareHours_successful() {
        Mockito.when(taRepository.existsById(TAid)).thenReturn(true);
        String validDate = "15/08/2022";
        int hours = 3;

        Assertions.assertEquals(true, workingHoursController.declareHours(TAid, validDate, hours).block());
        verify(workingHoursRepository).save(any(WorkingHour.class));
    }

    @Test
    public void declareHours_invalidTAId() {
        Mockito.when(taRepository.existsById(TAid)).thenReturn(false);
        String validDate = "15/08/2022";
        int hours = 3;
        Exception exception = Assertions.assertThrows(ResponseStatusException.class, () -> workingHoursController.declareHours(TAid, validDate, hours));

        String expectedMessage = "given TA id does not exist";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
        verify(workingHoursRepository, never()).save(any(WorkingHour.class));
    }

    @Test
    public void declareHours_negativeHours() {
        Mockito.when(taRepository.existsById(TAid)).thenReturn(true);
        String validDate = "15/08/2022";
        int hours = -3;
        Exception exception = Assertions.assertThrows(ResponseStatusException.class, () -> workingHoursController.declareHours(TAid, validDate, hours));

        String expectedMessage = "number of hours must be positive";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
        verify(workingHoursRepository, never()).save(any(WorkingHour.class));
    }

    @Test
    public void declareHours_invalidDateFormat() {
        Mockito.when(taRepository.existsById(TAid)).thenReturn(true);
        String validDate = "15/0!558-2022";
        int hours = 3;

        Assertions.assertEquals(false, workingHoursController.declareHours(TAid, validDate, hours).block());
        verify(workingHoursRepository, never()).save(any(WorkingHour.class));
    }

    @Test
    public void deleteHours_successful() {
        Assertions.assertEquals(true, workingHoursController.deleteHours(TAid).block());
    }

}
