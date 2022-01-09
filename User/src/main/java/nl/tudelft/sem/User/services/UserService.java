package nl.tudelft.sem.User.services;



import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import nl.tudelft.sem.DTO.ApplicationDTO;
import nl.tudelft.sem.DTO.ApplyingStudentDTO;
import nl.tudelft.sem.DTO.LeaveRatingDTO;
import nl.tudelft.sem.User.entities.Role;
import nl.tudelft.sem.User.entities.User;
import nl.tudelft.sem.User.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
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

    public List<ApplicationDTO> getAllApplications(UUID courseId)  {
        WebClient webClient = WebClient.create("http://localhost:47113");
        Flux<ApplicationDTO> applications = webClient.get()
                .uri("application/retrieveAll/" + courseId)
                .retrieve()
                .bodyToFlux(ApplicationDTO.class);
        return applications.toStream().collect(Collectors.toList());
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
        return applications.toStream().collect(Collectors.toList());
    }

    /** Requests the Application microservice to remove the application with the corresponding IDs.
     *
     * @param studentId the ID of the student that the application is linked to.
     * @param courseId the ID of the course that the application is linked to.
     * @return a boolean of whether or not the operation was successful.
     * @throws Exception is thrown when no boolean value was received.
     */
    public Boolean removeApplication(UUID studentId,UUID courseId, int port) throws Exception {
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Mono<Boolean> success = webClient.delete()
                .uri("/application/removeApplication/" + studentId + "/" + courseId)
                .retrieve()
                .bodyToMono(Boolean.class);
        Optional<Boolean> result = success.blockOptional();
        if (result.isEmpty()) {
            throw new Exception("No response received");
        }
        return result.get();
    }

    /**
     * Accepts an application for a given student and course, making them a TA.
     * @param applicationId of application to accept
     * @param port of application microservice.
     * @return if accept was successful.
     */

    public boolean acceptApplication(UUID applicationId, int port){
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Mono<Boolean> application = webClient.get()
                .uri("application/acceptApplication/"+ applicationId)
                .retrieve()
                .bodyToMono(Boolean.class);
        return application.block();
    }

    /** Add rating to TA by making a request to the TA microservice
     *
     * @param id        ID of TA we want to rate
     * @param rating    rating we would like to add.
     * @return bool that is true iff operation was successful.
     * @throws Exception if TA microservice does not respond.
     */
    public boolean addRatingByTAId(UUID id, int rating) throws Exception {
        // Create DTO to be body of request
        LeaveRatingDTO dto = new LeaveRatingDTO(id, Optional.of(rating));
        // Make request to TA microservice (port: 47110)
        WebClient webClient = WebClient.create("localhost:47110");
        Mono<Boolean> result = webClient.post()
                .uri("TA/addRating/")
                .body(Mono.just(dto), LeaveRatingDTO.class)
                .retrieve()
                .bodyToMono(Boolean.class);
        Optional<Boolean> success = result.blockOptional();
        if(success.isPresent()) {
            return success.get();
        } else {
            throw new Exception("No response from microservice!");
        }
    }

    public boolean validateRole(UUID userId, Role role) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("user not found"));
            return user.getRole() == role;
        } catch (Exception e) {
            return false;
        }
    }
}
