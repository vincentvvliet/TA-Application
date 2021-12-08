package nl.tudelft.sem.Application.services;

import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.services.validator.IsCourseOpen;
import nl.tudelft.sem.Application.services.validator.IsGradeSufficient;
import nl.tudelft.sem.Application.services.validator.IsUniqueApplication;
import nl.tudelft.sem.Application.services.validator.Validator;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.management.InvalidApplicationException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationServices {
    //TODO implement webclient in course services
    private final RestTemplate restTemplate;

    public ApplicationServices() {
        restTemplate = new RestTemplate();
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

}
