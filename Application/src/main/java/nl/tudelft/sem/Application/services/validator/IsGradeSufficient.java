package nl.tudelft.sem.Application.services.validator;


import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InvalidApplicationException;
import java.util.Optional;

@Service
public class IsGradeSufficient extends BaseValidator {
    @Autowired
    private ApplicationService applicationServices;

    /**
     *
     * @param application the application instance that needs to be checked
     * @return True if the grade the application is linked to is sufficient ( grade < 6)
     * @throws Exception if the application does not fulfil the requirements
     */
    @Override
    public Boolean handle(Application application) throws Exception {
        Double grade = applicationServices.getGrade(application.getStudentId(), application.getCourseId());
        if(grade == null){
            throw new Exception("could not retrieve course grade with the given student and course IDs");
        }
        if(grade < 6){
            throw new Exception("Grade was not sufficient");
        }
        return true;
    }
}
