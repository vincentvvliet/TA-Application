package nl.tudelft.sem.Application.services;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.management.InvalidApplicationException;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.validator.IsCourseOpen;
import nl.tudelft.sem.Application.services.validator.IsGradeSufficient;
import nl.tudelft.sem.Application.services.validator.IsUniqueApplication;
import nl.tudelft.sem.Application.services.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;


    //public ApplicationService() { }

    /**
     * Check if the ration of 1 TA for every 20 students is already met.
     *
     * @return true is ratio is already met, false otherwise.
     */
    public boolean isTASpotAvailable(UUID courseId) {
        return true;
    }

    /**
     * Creates a new TA once an application has been accepted.
     *
     * @param studentId of the student that becomes TA.
     * @param courseId of the course for which student is TA.
     *
     * @return true if the TA was successfully created.
     */
    public boolean createTA(UUID studentId, UUID courseId) {
        WebClient client = WebClient.create();
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = client.method(HttpMethod.POST);
        URI uri = URI.create("localhost:47112/TA/createTA/" + studentId  + "/" + courseId);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(uri);
        Mono<Boolean> response = bodySpec.retrieve().bodyToMono(Boolean.class);
        Optional<Boolean> result = response.blockOptional(Duration.of(1000, ChronoUnit.MILLIS));
        return result.orElse(false);
    }

    /** Ask the Course microservice for the grade corresponding to
     * the student and course ID of the application.
     *
     * @return A Optional double
     */
    public Double getGrade(UUID studentId, UUID courseId) {
        WebClient webClient = WebClient.create("localhost:47112");
        Mono<Double> rating = webClient.get()
                .uri("/grade/getGrade/" + studentId + "/" + courseId)
                .retrieve()
                .bodyToMono(Double.class);
        return rating.block();
    }

    /** Ask the Course microservice for the startDate corresponding to
     * the course ID of the application.
     *
     * @return An optional LocalDate
     */
    public LocalDate getCourseStartDate(UUID courseId) {
        WebClient webClient = WebClient.create("localhost:47112");
        Mono<LocalDate> rating = webClient.get()
                .uri("/course/getCourseStartDate/" + courseId)
                .retrieve()
                .bodyToMono(LocalDate.class);
        return rating.block();
    }

    /** Check if the application is valid.
     *
     * @return true if valid, false if not.
     */
    public boolean validate(Application application) {
        Boolean isValid = false;
        try {
            Validator validator = new IsCourseOpen(); // create chain of responsibility
            validator.setLast(new IsGradeSufficient());
            validator.setLast(new IsUniqueApplication());

            isValid = validator.handle(application);
        } catch (InvalidApplicationException e) {
            e.printStackTrace();
        }
        return isValid;
    }

    /** Get a list of applications by CourseId.
     *
     * @return List of Applications
     */
    public List<Application> getApplicationsByCourse(UUID course) {
        return applicationRepository.findAllApplicationsByCourseId(course);
    }
}
