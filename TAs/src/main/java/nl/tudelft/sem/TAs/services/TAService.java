package nl.tudelft.sem.TAs.services;

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

    public Mono<RatingDTO> getAverageRating(UUID studentId) {
        List<TA> TAs = taRepository.findAllByStudentId(studentId);
        if(!TAs.isEmpty()) {
            int sum = 0;
            for(TA ta:TAs) {
                sum += ta.getRating();
            }
            RatingDTO dto = new RatingDTO();
            dto.setRating(Optional.of(sum / TAs.size()));
            dto.setStudentId(studentId);
            return Mono.just(dto);
        }
        return Mono.empty();
    }
}