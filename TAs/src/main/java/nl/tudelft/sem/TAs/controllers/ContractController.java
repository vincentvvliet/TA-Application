package nl.tudelft.sem.TAs.controllers;

import nl.tudelft.sem.portConfiguration.PortData;
import nl.tudelft.sem.TAs.entities.Contract;
import nl.tudelft.sem.TAs.repositories.ContractRepository;
import nl.tudelft.sem.TAs.services.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/contract/")
@Controller
public class ContractController {

    private PortData portData = new PortData();

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractService contractService;

    /**
     * GET endpoint retrieves contract by id
     * @param id (UUID) of the contract
     * @return optional of contract
     */
    @GetMapping("/getContract/{id}")
    public Mono<Optional<Contract>> getContractById(@PathVariable (value = "id") UUID id) {
        Optional<Contract> contract = contractRepository.findById(id);
        return Mono.just(contract);
    }

    /**
     * Send contract gets the contract by using the combination of studentId and courseId.
     * @param studentId of the student hired
     * @param courseId of the course the student is hired for
     * @return the contract.
     */
    @GetMapping("/sendContract/{studentId}/{courseId}")
    public Mono<String> sendContract(@PathVariable (value = "studentId") UUID studentId, @PathVariable (value = "courseId") UUID courseId) {
        Contract contract = contractRepository.findByStudentIdAndCourseId(studentId, courseId).orElseThrow(NoSuchElementException::new);
        return Mono.just(contract.toString());
    }

    /**
     * GET endpoint retrieves all existing contracts
     * @return list of contracts
     */
    @GetMapping("/getContracts")
    public Mono<List<Contract>> getContracts() {
        return Mono.just(contractRepository.findAll());
    }

    /**
     * POST endpoint creating a contract for a TA (identified by studentId and courseId),
     * sends notification to student of contract that it has been created.
     * @param studentId of the TA
     * @param courseId of the course the TA is hired for
     * @return the id of the contract saved in the database
     */
    @PostMapping("/createContract/{studentid}/{courseid}")
    public Mono<UUID> createContract(@PathVariable(value = "studentid") UUID studentId , @PathVariable(value = "courseid") UUID courseId) throws Exception {
        Contract c = new Contract(studentId,courseId);
        contractRepository.save(c);
        contractService.sendContractNotification(c.getStudentId(), c.getCourseId(), portData.getUserPort());
        return Mono.just(c.getId());
    }

    /**
     * DELETE endpoint deletes a contract by id
     * @param id of the contract
     * @return boolean representing if the deletion was successful or not
     */
    @DeleteMapping("deleteContract/{id}")
    public Mono<Boolean> deleteContract(@PathVariable (value = "id") UUID id) {
        try {
            contractRepository.deleteById(id);
            return Mono.just(true);
        } catch (Exception e) {
            return Mono.just(false);
        }
    }
}
