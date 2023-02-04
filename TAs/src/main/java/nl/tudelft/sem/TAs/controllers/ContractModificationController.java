package nl.tudelft.sem.TAs.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;
import nl.tudelft.sem.TAs.entities.Contract;
import nl.tudelft.sem.TAs.repositories.ContractRepository;
import nl.tudelft.sem.TAs.services.ContractService;
import nl.tudelft.sem.portConfiguration.PortData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contract/")
@Controller
public class ContractModificationController {

    private PortData portData = new PortData();

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractService contractService;

    /**
     * PATCH endpoint sets maxHours a TA can work on the contract, sends notification to user about updated contract.
     * @param id of the contract to be updated
     * @param  hours a TA can work
     */
    @PatchMapping("addHours/{id}/{maxHours}")
    @ResponseStatus(value = HttpStatus.OK)
    public void addHoursById(@PathVariable(value = "id") UUID id , @PathVariable(value = "maxHours") Integer hours) throws Exception {
        Contract contract = contractRepository.findById(id).orElseThrow(NoSuchElementException::new);
        contract.setMaxHours(hours);
        contractRepository.save(contract);
        contractService.sendContractNotification(contract.getStudentId(), contract.getCourseId(), portData.getUserPort());
    }

    /**
     * PATCH endpoint sets task description for contract, sends notification to user about updated contract.
     * @param id of the contract
     * @param task description
     */
    @PatchMapping("addTask/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void addTaskById(@PathVariable (value = "id")  UUID id, @RequestBody String task) throws Exception {
        Contract contract = contractRepository.findById(id).orElseThrow(NoSuchElementException::new);
        contract.setTaskDescription(task);
        contractRepository.save(contract);
        contractService.sendContractNotification(contract.getStudentId(), contract.getCourseId(), portData.getUserPort());
    }

    /**
     * PATCH endpoint sets the date when the contract starts, sends notification to user about updated contract.
     * @param id of the contract.
     * @param date representing the start time of the contract
     */
    @PatchMapping("addDate/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void addDateById(@PathVariable (value = "id")  UUID id, @RequestBody String date) throws Exception {
        Contract contract = contractRepository.findById(id).orElseThrow(NoSuchElementException::new);
        Date d = new SimpleDateFormat("dd/MM/yyyy").parse(date);
        contract.setDate(d);
        contractRepository.save(contract);
        contractService.sendContractNotification(contract.getStudentId(), contract.getCourseId(), portData.getUserPort());
    }

    /**
     * PATCH endpoint sets the salary the TA will get per hour
     * @param id of the contract.
     * @param salary (per hour) the TA will get
     */
    @PatchMapping("addSalary/{id}/{salary}")
    @ResponseStatus(value = HttpStatus.OK)
    public void addSalaryById(@PathVariable (value = "id")  UUID id, @PathVariable(value = "salary") double salary) throws Exception {
        Contract contract = contractRepository.findById(id).orElseThrow(NoSuchElementException::new);
        contract.setSalaryPerHour(salary);
        contractRepository.save(contract);
        contractService.sendContractNotification(contract.getStudentId(), contract.getCourseId(), portData.getUserPort());
    }
}
