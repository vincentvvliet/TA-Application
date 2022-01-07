package nl.tudelft.sem.TAs.controllers;

import nl.tudelft.sem.TAs.entities.WorkingHour;
import nl.tudelft.sem.TAs.repositories.TARepository;
import nl.tudelft.sem.TAs.repositories.WorkingHoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@RestController
@RequestMapping("/hours/")
@Controller
public class WorkingHoursController {

    @Autowired
    WorkingHoursRepository workingHoursRepository;

    @Autowired
    TARepository taRepository;

    /**
     * POST endpoint declaring a batch of working hours for a TA (identified by TAid)
     * @param TAId of the TA
     * @param date when the work was performed (dd/MM/yyyy)
     * @param hours number of work hours performed by the TA
     * @return true if the hours are successfully declared, false otherwise
     */
    @PostMapping("/declareHours/{TAid}/{date}/{hours}")
    @ResponseStatus(value = HttpStatus.OK)
    public Mono<Boolean> declareHours(@PathVariable(value = "TAid") UUID TAId , @PathVariable(value = "date") String date, @PathVariable(value = "hours") int hours) {
        if (! taRepository.existsById(TAId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "given TA id does not exist");
        }
        if (hours <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "number of hours must be positive");

        }
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (DateTimeParseException e) {
            return Mono.just(false);
        }
        WorkingHour workingHour = new WorkingHour(TAId, parsedDate, hours);
        workingHoursRepository.save(workingHour);
        return Mono.just(true);
    }

    /**
     * DELETE endpoint deletes a specific working hours batch by id
     * @param id (UUID) of the working hours
     * @return true if successfully deleted, false otherwise
     */
    @DeleteMapping("/deleteHours/{id}")
    public Mono<Boolean> deleteHours(@PathVariable(value = "id") UUID id) {
        try {
            workingHoursRepository.deleteById(id);
            return Mono.just(true);
        } catch (IllegalArgumentException e) {
            return Mono.just(false);
        }
    }





}
