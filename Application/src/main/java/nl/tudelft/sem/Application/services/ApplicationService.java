package nl.tudelft.sem.Application.services;

import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.validator.IsCourseOpen;
import nl.tudelft.sem.Application.services.validator.IsGradeSufficient;
import nl.tudelft.sem.Application.services.validator.IsUniqueApplication;
import nl.tudelft.sem.Application.services.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.management.InvalidApplicationException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    //TODO implement webclient in course services
    private final RestTemplate restTemplate;

    public ApplicationService() {
        restTemplate = new RestTemplate();
    }

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

    /** Ask the Course microservice for the grade corresponding to the student and course ID of the application
     *
     * @return A Optional double
     */
    public Optional<Double> getGrade(UUID studentId, UUID courseId){
        String uri = "localhost:47112/grade/getGrade/" + studentId  + "/" + courseId ;
        return restTemplate.getForObject(uri, Optional.class);
    }

    /** Ask the Course microservice for the startDate corresponding to the course ID of the application
     *
     * @return An optional LocalDate
     */
    public Optional<LocalDate> getCourseStartDate(UUID courseId){
        String uri = "localhost:47112/course/getCourseStartDate/" + courseId;
        return restTemplate.getForObject(uri, Optional.class);
    }

    /** check if the application is valid
     *
     * @return true if valid, false if not
     */
    public static boolean validate(Application application){
        Boolean isValid = false;
        try{
            Validator validator = new IsCourseOpen(); // create chain of responsibility
            validator.setLast(new IsGradeSufficient());
            validator.setLast(new IsUniqueApplication());

            isValid = validator.handle(application);
        } catch (InvalidApplicationException e){
            e.printStackTrace();
        }
        return isValid;
    }

    public List<Application> getApplications() {
        return applicationRepository.findAll();
    }

    public List<Application> getApplicationsByCourse(UUID course) {
        return applicationRepository.findAllApplicationsByCourseId(course);
    }
}
