package nl.tudelft.sem.TAs.services;

import nl.tudelft.sem.DTO.LeaveRatingDTO;
import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.TAs.entities.Contract;
import nl.tudelft.sem.TAs.entities.TA;
import nl.tudelft.sem.TAs.repositories.ContractRepository;
import nl.tudelft.sem.TAs.repositories.TARepository;
import nl.tudelft.sem.portConfiguration.PortData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class TAService {
    @Autowired
    TARepository taRepository;

    @Autowired
    private ContractRepository contractRepository;

    /**
     * Gets the average TA rating for a given student
     * @param studentId of the student whose average TA rating is returned
     * @return mono of RatingDTO (containing studentId and rating)
     */
    public Mono<RatingDTO> getAverageRating(UUID studentId) {
        Optional<Integer> averageRating = taRepository.getAverageRating(studentId);
        if(averageRating.isPresent()) {
            RatingDTO dto = new RatingDTO();
            dto.setRating(averageRating.get());
            dto.setStudentId(studentId);
            return Mono.just(dto);
        }
        return Mono.empty();
    }

    /**
     * Indicates if the given course is finished or not
     * @param courseId of the course
     * @return true if course is finished, false otherwise
     */
    public boolean isCourseFinished(UUID courseId, int port) {
        WebClient webClient = WebClient.create("http://localhost:" + port); // 47112
        Mono<LocalDate> response = webClient.get()
                .uri("/course/getCourseEndDate/" + courseId)
                .retrieve()
                .bodyToMono(LocalDate.class);

        Optional<LocalDate> endDate = response.blockOptional();
        if (endDate.isEmpty()) {
            return false;
        } else {
            return endDate.get().isBefore(LocalDate.now());
        }
    }

    /**
     * Adds the average time spent by a TA on a course
     * @param optionalTA TA who adds the time spent (or possibly null)
     * @param timeSpent average number of hours spent per week
     * @return Mono of true if time added successfully, false otherwise
     */
    public Mono<Boolean> addTimeSpent(Optional<TA> optionalTA, Integer timeSpent) {
        if (timeSpent == null || timeSpent <= 0) {
            return Mono.just(false);
        }
        if (optionalTA.isEmpty()) {
            return Mono.just(false);
        }
        TA ta = optionalTA.get();
        if (! isCourseFinished(ta.getCourseId(), new PortData().getCoursePort())) {
            return Mono.just(false);
        }
        ta.setTimeSpent(timeSpent);
        taRepository.save(ta);
        return Mono.just(true);
    }

    /**
     * Adds the hiring contract for a TA
     * @param idTA id of TA whose contract is added
     * @param idContract id of contract to add
     * @return Mono of true if contract added successfully, false otherwise
     */
    public Mono<Boolean> addContract(UUID idTA, UUID idContract) {
        Optional<TA> optionalTA = taRepository.findById(idTA);
        Optional<Contract> optionalContract = contractRepository.findById(idContract);
        if (optionalTA.isEmpty() || optionalContract.isEmpty()) {
            return Mono.just(false);
        }
        optionalTA.get().setContract(optionalContract.get());
        taRepository.save(optionalTA.get());
        return Mono.just(true);
    }

    public Mono<Boolean> addRating(LeaveRatingDTO ratingDTO) {
        UUID taId = ratingDTO.getId();
        int rating = ratingDTO.getRating().get();
        Optional<TA> rated = taRepository.findById(taId);
        if (rated.isPresent()) {
            TA ta = rated.get();
            ta.setRating(rating);
            taRepository.save(ta);
            return Mono.just(true);
        }
        return Mono.just(false);
    }
}
