package nl.tudelft.sem.Application.services.validator;

import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InvalidApplicationException;
import java.util.Optional;

@Service
public class IsUniqueApplication extends BaseValidator{
    @Autowired
    private ApplicationRepository applicationRepository;

    /** checks if there is already an instance of this application in the database and returns false if that is the case
     *
     * @param application the application that is checked
     * @return true if there is no instance of this application present in the database
     * @throws InvalidApplicationException when there is an instance of this application in the database
     */
    @Override
    public Boolean handle(Application application) throws InvalidApplicationException {
         Optional<Application> application1 = applicationRepository
                 .findApplicationByStudentIdAndCourseId(application.getStudentId(),application.getCourseId());
         if(application1.isPresent()){
             throw new InvalidApplicationException("There already exists identical application");
         }
        return super.checkNext(application);
    }
}
