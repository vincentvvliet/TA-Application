package nl.tudelft.sem.TAs.controllers;

import java.util.*;
import nl.tudelft.sem.DTO.LeaveRatingDTO;
import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.TAs.entities.Contract;
import nl.tudelft.sem.TAs.entities.TA;
import nl.tudelft.sem.TAs.repositories.ContractRepository;
import nl.tudelft.sem.TAs.repositories.TARepository;
import nl.tudelft.sem.TAs.services.TAService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TAControllerTests {

    @Autowired
    TAController taController;

    @MockBean
    TAService taService;

    @MockBean
    TARepository taRepository;

    @MockBean
    ContractRepository contractRepository;

    UUID studentId;
    TA ta = new TA();
    List<TA> TA_list = new ArrayList<>();

    @BeforeEach
    void setup() {
        studentId = UUID.randomUUID();
        TA_list.add(ta);
        when(taRepository.findById(studentId)).thenReturn(Optional.ofNullable(ta));
    }

    @Test
    void getAverageRating_test() {
        // Act
        taController.getAverageRating(studentId);
        // Assert
        verify(taService).getAverageRating(studentId);
    }
    @Test
    void getTATest() {
        Assertions.assertEquals(ta,taController.getTAById(studentId).block());
    }
    @Test
    void getAllTAsTest() {
        when(taRepository.findAll()).thenReturn(TA_list);
        Assertions.assertEquals(TA_list,taController.getTAs().toStream().collect(Collectors.toList()));
    }
    @Test
    void createTATest() {
        UUID contractId = UUID.randomUUID();
        taController.createTA(studentId,contractId);
        verify(taRepository).save(any(TA.class));
    }
    @Test
    void addContractTest() {
        UUID contractId = UUID.randomUUID();
        Contract contract = new Contract();
        when(contractRepository.findById(contractId)).thenReturn(Optional.ofNullable(contract));
        taController.addContract(studentId,contractId);
        Assertions.assertEquals(contract,ta.getContract());
    }
    @Test
    public void deleteTest() {
        taController.deleteTA(studentId);
        verify(taRepository).deleteById(studentId);
    }


}
