package nl.tudelft.sem.TAs.controllers;

import nl.tudelft.sem.TAs.entities.Contract;
import nl.tudelft.sem.TAs.repositories.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/contract/")
public class ContractController {
    @Autowired
    private ContractRepository contractRepository;

    /**
     * GET endpoint retrieves contract by id
     * @param id (UUID) of the contract
     * @return optional of contract
     */
    @GetMapping("/getContract/{id}")
    public Optional<Contract> getContractById(@PathVariable (value = "id") UUID id) {
        return contractRepository.findById(id);
    }

    /**
     * GET endpoint retrieves all existing contracts
     * @return list of contracts
     */
    @GetMapping("/getContracts")
    public List<Contract> getContracts() {
        return contractRepository.findAll();
    }

    /**
     * POST endpoint creating a contract for a TA (identified by studentId and courseId)
     * @param studentId of the TA
     * @param courseId of the course the TA is hired for
     * @return true after the contract is created and saved in the database
     */
    @PostMapping("/createContract/{studentid}/{courseid}")
    public boolean createContract(@PathVariable(value = "studentid") UUID studentId , @PathVariable(value = "courseid") UUID courseId) {
        Contract c = new Contract(studentId,courseId);
        contractRepository.save(c);
        return true;
    }

    /**
     * PATCH endpoint sets maxHours a TA can work on the contract
     * @param id of the contract to be updated
     * @param  hours a TA can work
     */
    @PatchMapping("addHours/{id}/{maxHours}")
    @ResponseStatus(value = HttpStatus.OK)
    public void addHoursbyId(@PathVariable (value = "id")  UUID id , @PathVariable(value = "maxHours") Integer hours) {
        Contract contract = contractRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        contract.setMaxHours(hours);
        contractRepository.save(contract);
    }

    /**
     * PATCH endpoint sets task description for contract
     * @param id of the contract
     * @param task description
     */
    @PatchMapping("addTask/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void addTaskById(@PathVariable (value = "id")  UUID id, @RequestBody String task) {
        Contract contract = contractRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        contract.setTaskDescription(task);
        contractRepository.save(contract);
    }

    /**
     * PATCH endpoint sets the date when the contract starts?
     * @param id of the contract
     * @param date representing the start time of the contract
     */
    @PatchMapping("addDate/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void addDateById(@PathVariable (value = "id")  UUID id, @RequestBody String date) throws ParseException {
        Contract contract = contractRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        Date d = new SimpleDateFormat("dd/MM/yyyy").parse(date);
        contract.setDate(d);
        contractRepository.save(contract);
    }

    /**
     * PATCH endpoint sets the salary the TA will get per hour
     * @param id of the contract
     * @param salary (per hour) the TA will get
     */
    @PatchMapping("addSalary/{id}/{salary}")
    @ResponseStatus(value = HttpStatus.OK)
    public void addSalaryById(@PathVariable (value = "id")  UUID id, @PathVariable(value = "salary") double salary) {
        Contract contract = contractRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        contract.setSalaryPerHour(salary);
        contractRepository.save(contract);
    }

    /**
     * DELETE endpoint deletes a contract by id
     * @param id of the contract
     * @return boolean representing if the deletion was successful or not
     */
    @DeleteMapping("deleteContract/{id}")
    public boolean deleteContract(@PathVariable (value = "id") UUID id) {
        try {
            contractRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
