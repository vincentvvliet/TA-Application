package nl.tudelft.sem.TAs.services;

import nl.tudelft.sem.DTO.LeaveRatingDTO;
import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.TAs.entities.TA;
import nl.tudelft.sem.TAs.repositories.TARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TAService {
    @Autowired
    TARepository taRepository;

    /**
     * Gets the average TA rating for a given student
     * @param studentId of the student whose average TA rating is returned
     * @return mono of RatingDTO (containing studentId and rating)
     */
    public Mono<RatingDTO> getAverageRating(UUID studentId) {
        Optional<Integer> averageRating = taRepository.getAverageRating(studentId);
        if(averageRating.isPresent()) {
            RatingDTO dto = new RatingDTO();
            dto.setRating(averageRating);
            dto.setStudentId(studentId);
            return Mono.just(dto);
        }
        return Mono.empty();
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
