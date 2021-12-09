package nl.tudelft.sem.Application.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.exceptions.EmptyResourceException;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ApplicationService {

    @Autowired
    public ApplicationRepository applicationRepository;

    /** getRatingForTA method.
     * Makes request to TA service for a rating.
     *
     * @param studentId studentId of TA we want the rating for.
     * @param courseId courseId of the course we want the rating for.
     *
     * @return rating of TA for a certain course.
     * @throws EmptyResourceException if the TA service returns an empty result.
     */
    public int getRatingForTA(UUID studentId, UUID courseId) throws EmptyResourceException {
        WebClient webClient = WebClient.create("localhost:47110");
        Mono<Integer> rating = webClient.get()
                .uri("/TA/getRating/" + studentId + "/" + courseId)
                .retrieve()
                .bodyToMono(Integer.class);
        Optional<Integer> result = rating.blockOptional();
        if (result.isEmpty()) {
            throw new EmptyResourceException("Result was empty");
        }
        return result.get();
    }
}
