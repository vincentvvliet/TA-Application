package nl.tudelft.sem.TAs.controllers;

import nl.tudelft.sem.TAs.entities.Contract;
import nl.tudelft.sem.TAs.repositories.ContractRepository;
import nl.tudelft.sem.TAs.services.ContractService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
public class ContractControllerTest {

    @Autowired
    ContractController contractController;

    @MockBean
    ContractRepository contractRepository;

    @MockBean
    ContractService contractService;

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
    public void findAllTest() throws Exception {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        when(contractRepository.findAll()).thenReturn(contractList);
        contractService.sendContractNotification(studentId, courseId, 47111);
        Assertions.assertEquals(contractController.getContracts().block(), contractList);

    }

    @Test
    public void createTest() throws Exception {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        when(contractService.sendContractNotification(studentId, courseId, 47111)).thenReturn(true);
        contractController.createContract(studentId, courseId);
        verify(contractRepository).save(any(Contract.class));
    }

    @Test
    public void deleteTest() {
        contractController.deleteContract(id);
        verify(contractRepository).deleteById(id);
    }

    @Test
    public void addHoursTest() throws Exception {
        contractController.addHoursById(id,42);
        Assertions.assertEquals(42,contract.getMaxHours());
    }

    @Test
    public void addTaskTest() throws Exception {
        String task = "TA needs to grade exam";
        contractController.addTaskById(id,task);
        Assertions.assertEquals(task , contract.getTaskDescription());
    }

    @Test
    public void addSalaryTest() throws Exception {
        double salary = 13.5;
        contractController.addSalaryById(id,salary);
        Assertions.assertEquals(salary , contract.getSalaryPerHour());

    }

}
