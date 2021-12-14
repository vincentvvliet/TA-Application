package nl.tudelft.sem.Application.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.exceptions.EmptyResourceException;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.validator.IsCourseOpen;
import nl.tudelft.sem.Application.services.validator.IsGradeSufficient;
import nl.tudelft.sem.Application.services.validator.IsUniqueApplication;
import nl.tudelft.sem.Application.services.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private IsCourseOpen isCourseOpen;

    @Autowired
    private IsUniqueApplication isUniqueApplication;

    @Autowired
    private IsGradeSufficient isGradeSufficient;

    private Validator validator;


    /**
     * Check if the ration of 1 TA for every 20 students is already met.
     *
     * @return true is ratio is already met, false otherwise.
     */
    public boolean isTASpotAvailable(@SuppressWarnings("unused") UUID courseId) {
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
        WebClient webClient = WebClient.create("http://localhost:47110");
        Mono<Boolean> rating = webClient.get()
                .uri("/TA/createTA/" + studentId  + "/" + courseId)
                .retrieve()
                .bodyToMono(Boolean.class);
        return rating.blockOptional().orElse(false);
    }

    /** Ask the Course microservice for the grade corresponding to
     * the student and course ID of the application.
     *
     * @return A Optional double
     */
    public Double getGrade(UUID studentId, UUID courseId) throws EmptyResourceException {
        WebClient webClient = WebClient.create("http://localhost:47112");
        Mono<Double> rating = webClient.get()
                .uri("/grade/getGrade/" + studentId + "/" + courseId)
                .retrieve()
                .bodyToMono(Double.class);
        Optional<Double> result = rating.blockOptional();
        if (result.isEmpty()) {
            throw new EmptyResourceException("no grade found");
        }

        return result.get();
    }

    /** Ask the Course microservice for the startDate corresponding to
     * the course ID of the application.
     *
     * @return An optional LocalDate
     */
    public LocalDate getCourseStartDate(UUID courseId) throws EmptyResourceException {
        WebClient webClient = WebClient.create("http://localhost:47112");
        Mono<LocalDate> rating = webClient.get()
                .uri("/course/getCourseStartDate/" + courseId)
                .retrieve()
                .bodyToMono(LocalDate.class);

        Optional<LocalDate> result = rating.blockOptional();
        if (result.isEmpty()) {
            throw new EmptyResourceException("no TA rating found");
        }
        return result.get();
    }

    /** Check if the application is valid.
     *
     * @return true if valid, false if not.
     */
    public boolean validate(Application application) {
        validator.setLast(isCourseOpen); // create chain of responsibility
        validator.setLast(isGradeSufficient);
        validator.setLast(isUniqueApplication);
        Boolean isValid = false;
        try {
            isValid = validator.checkNext(application);
        } catch (Exception e) {
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
