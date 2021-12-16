package nl.tudelft.sem.User.services;

import nl.tudelft.sem.DTO.ApplicationDTO;
import nl.tudelft.sem.DTO.ApplyingStudentDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

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
}
