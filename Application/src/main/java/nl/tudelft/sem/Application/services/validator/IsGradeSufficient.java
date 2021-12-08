package nl.tudelft.sem.Application.services.validator;


import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.services.ApplicationService;
import org.springframework.stereotype.Service;

import javax.management.InvalidApplicationException;
import java.util.Optional;

@Service
public class IsGradeSufficient extends BaseValidator {
    private final ApplicationService applicationServices;

    public IsGradeSufficient() {
        applicationServices = new ApplicationService();
    }

    /**
     *
     * @param application the application instance that needs to be checked
     * @return True if the grade the application is linked to is sufficient ( grade < 6)
     * @throws InvalidApplicationException if the application does not fulfil the requirements
     */
    @Override
    public Boolean handle(Application application) throws InvalidApplicationException {
        Optional<Double> grade1 = applicationServices.getGrade(application.getStudentId(), application.getCourseId());
        if(grade1.isEmpty()){
            throw new InvalidApplicationException("could not retrieve course grade with the given student and course IDs");
        }
        double grade = grade1.get();
        if(grade < 6){
            throw new InvalidApplicationException("Grade was not sufficient");
        }
        return true;
    }
}
