package nl.tudelft.sem.User.services;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import nl.tudelft.sem.DTO.LeaveRatingDTO;
import nl.tudelft.sem.User.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import nl.tudelft.sem.DTO.ApplicationDTO;
import nl.tudelft.sem.DTO.ApplyingStudentDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    /**
     * Accepts the specified TA application.
     *
     * @param applicationId of the TA application to be accepted
     * @return true if application successfully accepted, false otherwise
     */
    public boolean acceptTaApplication(UUID applicationId) {
        WebClient client = WebClient.create();
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.POST);
        WebClient.RequestBodySpec bodySpec =
                uriSpec.uri(URI.create(
                        "localhost:47112/application/acceptApplication/" + applicationId));
        Mono<Boolean> response = bodySpec.retrieve().bodyToMono(Boolean.class);
        Optional<Boolean> result = response.blockOptional(Duration.of(1000, ChronoUnit.MILLIS));
        return result.orElse(false);
    }


    /**
     *  Created an application using a userID and CourseId.
     *
     * @param userId of student creating application.
     * @param courseId of course being applied for.
     * @return boolean
     */
    public boolean createApplication(UUID userId, UUID courseId) {
        WebClient client = WebClient.create();
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec =
                client.method(HttpMethod.POST);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(URI.create(
                "localhost:47112/application/createApplication/" + userId + "/" + courseId));
        Mono<Boolean> response = bodySpec.retrieve().bodyToMono(Boolean.class);
        Optional<Boolean> result = response.blockOptional(Duration.of(1000, ChronoUnit.MILLIS));
        return result.orElse(false);
    }

    /**
     * Requests all applications from the Applications microservice.
     *  @return List of all Applications.
     */

    public List<ApplicationDTO> getAllApplications() {
        WebClient webClient = WebClient.create("http://localhost:47113");
        Flux<ApplicationDTO> applications = webClient.get()
                .uri("application/retrieveAll")
                .retrieve()
                .bodyToFlux(ApplicationDTO.class);
        return applications.collectList().block();
    }

    /**
     * Requests a specific student's Application for a specific course,
     * from Application microservice.
     *
     * @param studentId The student's ID.
     * @param courseId The course that is being applied for.
     * @return An application.
     */
    public ApplicationDTO getApplication(UUID studentId, UUID courseId) {
        WebClient webClient = WebClient.create("http://localhost:47113");
        Mono<ApplicationDTO> application = webClient.get()
                .uri("application/getApplication/" + studentId + "/" + courseId)
                .retrieve()
                .bodyToMono(ApplicationDTO.class);
        return application.block();
    }

    /**
     * Gets all applications for a course, with details on applying students.
     * @param courseId id of course.
     * @return list of applying students.
     */
    public List<ApplyingStudentDTO> getApplicationsOverview(UUID courseId) {
        WebClient webClient = WebClient.create("http://localhost:47113");
        Flux<ApplyingStudentDTO> applications = webClient.get()
                .uri("application/getApplicationOverview/" + courseId)
                .retrieve()
                .bodyToFlux(ApplyingStudentDTO.class);
        return applications.collectList().block();
    }

    public boolean addRatingByTAId(UUID ta_id, int rating) throws Exception {
        // Make request to TA microservice (port: 47110)
        WebClient webClient = WebClient.create("localhost:47110");
        Mono<Boolean> application = webClient.patch()
            .uri("TA/addRating/" + ta_id + "/" + rating)
            .retrieve()
            .bodyToMono(Boolean.class);
        Optional<Boolean> response = application.blockOptional();
        if(response.isEmpty()) {
            throw new Exception("No response retrieved!");
        }
        return response.get();
    }

    public boolean addRatingByTAId(UUID id, UUID courseId, int rating, UUID userId) throws Exception {
        // Create DTO to be body of request
        LeaveRatingDTO dto = new LeaveRatingDTO(id, courseId, Optional.of(rating));
        // Make request to TA microservice (port: 47110)
        WebClient webClient = WebClient.create("localhost:47110");
        Mono<Boolean> result = webClient.post()
            .uri("TA/addRating/" + userId)
            .body(Mono.just(dto), LeaveRatingDTO.class)
            .retrieve()
            .bodyToMono(Boolean.class);
        Optional<Boolean> succes = result.blockOptional();
        if(succes.isPresent()) {
            return succes.get();
        } else {
            throw new Exception("No response from microservice!");
        }
    }
}
