package nl.tudelft.sem.Application.services;

import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    /**
     * check if the ration of 1 TA for every 20 students is already met
     * @return true is ratio is already met, false otherwise
     */
    public boolean isTASpotAvailable(UUID courseId) {
        return true;
    }

    /**
     * creates a new TA once an application has been accepted
     * @param studentId of the student that becomes TA
     * @param courseId of the course for which student is TA
     * @return true if the TA was successfully created
     */
    public boolean createTA(UUID studentId, UUID courseId) {
        WebClient client = WebClient.create();
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.POST);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(URI.create("localhost:47112/TA/createTA/" + studentId  + "/" + courseId));
        Mono<Boolean> response = bodySpec.retrieve().bodyToMono(Boolean.class);
        Optional<Boolean> result = response.blockOptional(Duration.of(1000, ChronoUnit.MILLIS));
        return result.orElse(false);
    }
}
