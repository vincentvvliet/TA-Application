package nl.tudelft.sem.TAs.controllers;

import java.util.Optional;
import nl.tudelft.sem.TAs.entities.Contract;
import nl.tudelft.sem.TAs.entities.TA;
import nl.tudelft.sem.TAs.entities.WorkingHour;
import nl.tudelft.sem.TAs.repositories.ContractRepository;
import nl.tudelft.sem.TAs.repositories.TARepository;
import nl.tudelft.sem.TAs.repositories.WorkingHoursRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class WorkingHoursControllerTest {

    @Autowired
    WorkingHoursController workingHoursController;

    @MockBean
    WorkingHoursRepository workingHoursRepository;

    @MockBean
    ContractRepository contractRepository;

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
        // Arrange
        when(taRepository.findById(any())).thenReturn(
            Optional.of(new TA(UUID.randomUUID(), UUID.randomUUID()))
        );
        Contract contract = new Contract(UUID.randomUUID(), UUID.randomUUID());
        contract.setMaxHours(50);
        when(contractRepository.findByStudentIdAndCourseId(any(), any()))
            .thenReturn(Optional.of(contract));
        String validDate = "15/08/2022";
        int hours = 3;

        // Act & Assert
        assertEquals(true, workingHoursController.declareHours(TAid, validDate, hours).block());
        verify(workingHoursRepository).save(any(WorkingHour.class));
    }



    @Test
    public void declareHours_negativeHours() {
        // Arrange
        when(taRepository.findById(any())).thenReturn(
            Optional.of(new TA(UUID.randomUUID(), UUID.randomUUID()))
        );
        Contract contract = new Contract(UUID.randomUUID(), UUID.randomUUID());
        contract.setMaxHours(50);
        when(contractRepository.findByStudentIdAndCourseId(any(), any()))
            .thenReturn(Optional.of(contract));
        String validDate = "15/08/2022";
        int hours = -3;
        Exception exception = assertThrows(ResponseStatusException.class, () -> workingHoursController.declareHours(TAid, validDate, hours));

        String expectedMessage = "number of hours must be positive";
        String actualMessage = exception.getMessage();

        // Act & Assert
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
        verify(workingHoursRepository, never()).save(any(WorkingHour.class));
    }

    @Test
    public void declareHours_invalidDateFormat() {
        // Arrange
        when(taRepository.findById(any())).thenReturn(
            Optional.of(new TA(UUID.randomUUID(), UUID.randomUUID()))
        );
        Contract contract = new Contract(UUID.randomUUID(), UUID.randomUUID());
        contract.setMaxHours(50);
        when(contractRepository.findByStudentIdAndCourseId(any(), any()))
            .thenReturn(Optional.of(contract));
        String validDate = "15/0!558-2022";
        int hours = 3;

        // Act & Assert
        assertEquals(false, workingHoursController.declareHours(TAid, validDate, hours).block());
        verify(workingHoursRepository, never()).save(any(WorkingHour.class));
    }

    @Test
    public void deleteHours_successful() {
        assertEquals(true, workingHoursController.deleteHours(TAid).block());
    }

    @Test
    void declareHours_taNotFound_throwsBadRequest() {
        // Arrange
        when(taRepository.findById(any())).thenReturn(
            Optional.empty()
        );
        // Act
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () ->
            workingHoursController.declareHours(UUID.randomUUID(), "", 55)
        );
        // Assert
        assertEquals("given TA id does not exist", e.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, e.getStatus());
    }

    @Test
    void declareHours_contractNotFound_throwsNotFound() {
        // Arrange
        when(taRepository.findById(any())).thenReturn(
            Optional.of(new TA(UUID.randomUUID(), UUID.randomUUID()))
        );
        when(contractRepository.findByStudentIdAndCourseId(any(), any()))
            .thenReturn(Optional.empty());
        // Act
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () ->
            workingHoursController.declareHours(UUID.randomUUID(), "", 55)
        );
        // Assert
        assertEquals("TA does not have a contract", e.getReason());
        assertEquals(HttpStatus.NOT_FOUND, e.getStatus());
    }

    @Test
    void declareHours_hoursExceeded_throwsForbidden() {
        // Arrange
        when(taRepository.findById(any())).thenReturn(
            Optional.of(new TA(UUID.randomUUID(), UUID.randomUUID()))
        );
        Contract contract = new Contract(UUID.randomUUID(), UUID.randomUUID());
        contract.setMaxHours(50);
        when(contractRepository.findByStudentIdAndCourseId(any(), any()))
            .thenReturn(Optional.of(contract));
        // Act
        ResponseStatusException e = assertThrows(ResponseStatusException.class, () ->
            workingHoursController.declareHours(UUID.randomUUID(), "", 55)
        );
        // Assert
        assertEquals("number of hours must not exceed hours on contract", e.getReason());
        assertEquals(HttpStatus.FORBIDDEN, e.getStatus());
    }

}
