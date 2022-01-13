package nl.tudelft.sem.TAs.controllers;


import nl.tudelft.sem.DTO.LeaveRatingDTO;
import nl.tudelft.sem.portConfiguration.PortData;
import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.TAs.entities.Contract;
import nl.tudelft.sem.TAs.entities.TA;
import nl.tudelft.sem.TAs.repositories.ContractRepository;
import nl.tudelft.sem.TAs.repositories.TARepository;
import nl.tudelft.sem.TAs.services.TAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

// @AuthenticationPrincipal

@RestController
@RequestMapping("/TA/")
@Controller
public class TAController {

    @Autowired
    private TAService taService;

    @Autowired
    private TARepository taRepository;

    /**
     * GET endpoint retrieves TAs average rating by studentId.
     *
     * @param studentId (UUID) of the student
     * @return rating
     */
    @GetMapping("/getRating/{studentid}")
    public Mono<RatingDTO> getAverageRating(@PathVariable(value = "studentid") UUID studentId) {
        return taService.getAverageRating(studentId);
    }

    /**
     * GET endpoint retrieves TA by id.
     *
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
     * GET endpoint retrieves all existing TA.
     *
     * @return list of TAs
     */
    @GetMapping("/getTAs")
    public Flux<TA> getTAs() {
        return Flux.fromStream(taRepository.findAll().stream());
    }

    /**
     * POST endpoint creating a TA (identified by studentId and courseId).
     *
     * @param studentId of the student to be a TA
     * @param courseId of the course the TA is hired for
     * @return true after the TA is created and saved in the database
     */
    @PostMapping("/createTA/{studentid}/{courseid}")
    public Mono<Boolean> createTA(@PathVariable(value = "studentid") UUID studentId, @PathVariable(value = "courseid") UUID courseId) {
        TA ta = new TA(studentId, courseId);
        taRepository.save(ta);
        return Mono.just(true);
    }

    /**
     * POST Endpoint to add contract to TA.
     *
     * @param id of the TA
     * @param contractId of the contract attached to TA
     * @return true if the object was modified
     */
    @PostMapping("/addContract/{id}/{contractId}")
    @ResponseStatus(value = HttpStatus.OK)
    public Mono<Boolean> addContract(@PathVariable(value = "id") UUID id, @PathVariable(value = "contractId") UUID contractId) {
        Optional<TA> ta = taRepository.findById(id);
        return taService.addContract(ta, contractId);
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

    /**
     * PATCH endpoint sets actual time spent by a TA preparing for a course
     * @param id of the TA
     * @param timeSpent estimated average number of weekly hours spent by the TA working on the course
     * @return true if time was added successfully, false otherwise
     */
    @PatchMapping("addTimeSpent/{id}/{timeSpent}")
    @ResponseStatus(value = HttpStatus.OK)
    public Mono<Boolean> addTimeSpent(@PathVariable (value = "id")  UUID id , @PathVariable(value = "timeSpent") Integer timeSpent) {
        Optional<TA> ta = taRepository.findById(id);
        return taService.addTimeSpent(ta, timeSpent);
    }
    /**
     * POST endpoint for adding rating to a certain TA
     * Careful: Overwrites old rating.
     *
     * @param ratingDTO dto containing relevant information
     * @return boolean indicating success of operation
     */
    @PostMapping("addRating")
    Mono<Boolean> addRating(@RequestBody LeaveRatingDTO ratingDTO) {
        if (ratingDTO.getRating().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request is incomplete");
        }
        return taService.addRating(ratingDTO);
    }

    /**
     * GET endpoint retrieves average time spent from past TAs for a course
     * @param courseId (UUID) of the course
     * @return optional of double (average time spent)
     */
    @GetMapping("/getAverageTimeSpent/{id}")
    public Mono<Double> getAverageTimeSpentAsTA(@PathVariable(value = "id") UUID courseId) {
        Optional<Double> averageTime = taRepository.getAverageTimeSpentAsTA(courseId);
        if(averageTime.isPresent()){
            return Mono.just(averageTime.get());
        } else {
            return Mono.empty();
        }
    }


    /**
     * PUT endpoint for editing rating to a certain TA.
     *
     * @param ratingDTO dto containing relevant information.
     * @return boolean indicating success of operation.
     */
    @PutMapping("setRating")
    Mono<Boolean> setRating(@RequestBody LeaveRatingDTO ratingDTO) {
        if (ratingDTO.getRating().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request is incomplete");
        }
        return taService.addRating(ratingDTO);
    }
}
