package nl.tudelft.sem.Application.entities;

import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.management.InvalidApplicationException;
import java.util.Optional;

public class IsUniqueApplication extends BaseValidator{
    @Autowired
    private ApplicationRepository applicationRepository;

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
