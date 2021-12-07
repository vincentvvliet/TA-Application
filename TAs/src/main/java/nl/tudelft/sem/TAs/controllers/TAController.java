package nl.tudelft.sem.TAs.controllers;


import nl.tudelft.sem.TAs.entities.Contract;
import nl.tudelft.sem.TAs.entities.TA;
import nl.tudelft.sem.TAs.repositories.ContractRepository;
import nl.tudelft.sem.TAs.repositories.TARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/TA/")
public class TAController {
    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private TARepository taRepository;

    /**
     * GET endpoint retrieves TA by id
     * @param id (UUID) of the TA
     * @return optional of TA
     */
    @GetMapping("/getTA/{id}")
    public Optional<TA> getTAById(@PathVariable(value = "id") UUID id) {
        return taRepository.findById(id);
    }
    /**
     * GET endpoint retrieves all existing TA
     * @return list of TAs
     */
    @GetMapping("/getTAs")
    public List<TA> getTAs() {
        return taRepository.findAll();
    }

    /**
     * POST endpoint creating a TA (identified by studentId and courseId)
     * @param studentId of the student to be a TA
     * @param courseId of the course the TA is hired for
     * @return true after the TA is created and saved in the database
     */
    @PostMapping("/createTA/{studentid}/{courseid}")
    public boolean createTA(@PathVariable(value = "studentid") UUID studentId , @PathVariable(value = "courseid") UUID courseId) {
        TA ta = new TA(studentId,courseId);
        taRepository.save(ta);
        return true;
    }

    /**PATCH Endpoint to add contract to TA
     * @param id of the TA
     * @param contractId of the contract attached to TA
     * @return true if the object was modified
     */
    @RequestMapping("/addContract/{id}/{contractId}")
    @ResponseStatus(value = HttpStatus.OK)
    public boolean addContract(@PathVariable(value = "id") UUID id,@PathVariable(value = "contractId") UUID contractId) {
         TA ta = taRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
         Contract contract = contractRepository.findById(contractId).orElseThrow(() -> new NoSuchElementException());
         ta.setContract(contract);
         taRepository.save(ta);
         return true;
    }

    /**
     * PATCH endpoint sets TA rating
     * @param id of TA
     * @param rating of the TA
     */
    @PatchMapping("addRating/{id}/{rating}")
    @ResponseStatus(value = HttpStatus.OK)
    public void addRatingbyId(@PathVariable (value = "id")  UUID id, @PathVariable(value = "rating") int rating) {
        TA ta = taRepository.findById(id).orElseThrow(() -> new NoSuchElementException());
        ta.setRating(rating);
        taRepository.save(ta);
    }


    /**
     * DELETE endpoint deletes a TA by id
     * @param id of the TA
     * @return boolean representing if the deletion was successful or not
     */
    @DeleteMapping("deleteTA/{id}")
    public boolean deleteTA(@PathVariable (value = "id") UUID id) {
        try {
            taRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
