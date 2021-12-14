package nl.tudelft.sem.TAs.controllers;

import nl.tudelft.sem.TAs.entities.Contract;
import nl.tudelft.sem.TAs.repositories.ContractRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ContractControllerTest {

    @InjectMocks
    ContractController contractController;

    @Mock
    ContractRepository contractRepository;

    Contract contract = new Contract();
    List<Contract> contractList = new ArrayList<>();
    UUID id = UUID.randomUUID();

    @BeforeEach
    public void init() {
        contractList.add(contract);
        when(contractRepository.findById(id)).thenReturn(Optional.ofNullable(contract));
    }

    @Test
    public void findByIdTest() {
        Assertions.assertEquals(contractController.getContractById(id).block(), Optional.ofNullable(contract));
    }

    @Test
    public void findAllTest() {
        when(contractRepository.findAll()).thenReturn(contractList);
        Assertions.assertEquals(contractController.getContracts(),contractList);
    }

    @Test
    public void createTest() {
        contractController.createContract(UUID.randomUUID(),UUID.randomUUID());
        verify(contractRepository).save(any(Contract.class));
    }

    @Test
    public void deleteTest() {
        contractController.deleteContract(id);
        verify(contractRepository).deleteById(id);
    }

    @Test
    public void addHoursTest() {
        contractController.addHoursById(id,42);
        Assertions.assertEquals(42,contract.getMaxHours());
    }

    @Test
    public void addTaskTest() {
        String task = "TA needs to grade exam";
        contractController.addTaskById(id,task);
        Assertions.assertEquals(task , contract.getTaskDescription());
    }

    @Test
    public void addSalaryTest() {
        double salary = 13.5;
        contractController.addSalaryById(id,salary);
        Assertions.assertEquals(salary , contract.getSalaryPerHour());

    }

}
