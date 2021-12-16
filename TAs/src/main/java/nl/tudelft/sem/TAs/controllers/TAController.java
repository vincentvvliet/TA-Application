package nl.tudelft.sem.TAs.controllers;


import nl.tudelft.sem.DTO.ApplyingStudentDTO;
import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.TAs.entities.Contract;
import nl.tudelft.sem.TAs.entities.TA;
import nl.tudelft.sem.TAs.repositories.ContractRepository;
import nl.tudelft.sem.TAs.repositories.TARepository;
import nl.tudelft.sem.TAs.services.TAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

// @AuthenticationPrincipal

@RestController
@RequestMapping("/TA/")
@Controller
public class TAController {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private TAService taService;

    @Autowired
    private TARepository taRepository;

    /**
     * GET endpoint retrieves TAs average rating by studentId.
     * @param studentId (UUID) of the student
     * @return rating
     */
    @GetMapping("/getRating/{studentid}")
    public Mono<RatingDTO> getAverageRating(@PathVariable(value = "studentid") UUID studentId) {
        return taService.getAverageRating(studentId);
    }

    /**
     * GET endpoint retrieves TA by id
     * @param id (UUID) of the TA
     * @return mono optional of TA
     */
    @GetMapping("/getTA/{id}")
    public Mono<TA> getTAById(@PathVariable(value = "id") UUID id) {
        Optional<TA> ta = taRepository.findById(id);
        if(ta.isEmpty()){
            return Mono.empty();
        }
        return Mono.just(ta.get());
    }
    /**
     * GET endpoint retrieves all existing TA
     * @return list of TAs
     */
    @GetMapping("/getTAs")
    public Mono<List<TA>> getTAs() {
        return Mono.just(taRepository.findAll());
    }

    /**
     * POST endpoint creating a TA (identified by studentId and courseId)
     * @param studentId of the student to be a TA
     * @param courseId of the course the TA is hired for
     * @return true after the TA is created and saved in the database
     */
    @PostMapping("/createTA/{studentid}/{courseid}")
    public Mono<Boolean> createTA(@PathVariable(value = "studentid") UUID studentId , @PathVariable(value = "courseid") UUID courseId) {
        TA ta = new TA(studentId,courseId);
        taRepository.save(ta);
        return Mono.just(true);
    }

    /**PATCH Endpoint to add contract to TA
     * @param id of the TA
     * @param contractId of the contract attached to TA
     * @return true if the object was modified
     */
    @RequestMapping("/addContract/{id}/{contractId}")
    @ResponseStatus(value = HttpStatus.OK)
    public  Mono<Boolean> addContract(@PathVariable(value = "id") UUID id,@PathVariable(value = "contractId") UUID contractId) {
         TA ta = taRepository.findById(id).orElseThrow(NoSuchElementException::new);
         Contract contract = contractRepository.findById(contractId).orElseThrow(NoSuchElementException::new);
         ta.setContract(contract);
         taRepository.save(ta);
        return Mono.just(true);
    }
    /**
     * DELETE endpoint deletes a TA by id
     * @param id of the TA
     * @return boolean representing if the deletion was successful or not
     */
    @DeleteMapping("deleteTA/{id}")
    public Mono<Boolean> deleteTA(@PathVariable (value = "id") UUID id) {
        try {
            taRepository.deleteById(id);
            return Mono.just(true);
        } catch (Exception e) {
            return Mono.just(false);
        }
    }


}
