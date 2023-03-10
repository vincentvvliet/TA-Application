package nl.tudelft.sem.Application.services.validator;

import java.util.Optional;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IsUniqueApplication extends BaseValidator {
    @Autowired
    private ApplicationRepository applicationRepository;

    /** Checks if there is already an instance of this application in the database
     * and returns false if that is the case.
     *
     * @param application the application that is checked
     * @return true if there is no instance of this application present in the database
     * @throws Exception when there is an instance of this application in the database
     */
    @Override
    public Boolean handle(Application application) throws Exception {
        Optional<Application> application1 = applicationRepository
                 .findByStudentIdAndCourseId(application.getStudentId(),
                         application.getCourseId());
        if (application1.isPresent()) {
            throw new Exception("There already exists an application with that student and courseID");
        }
        return super.checkNext(application);
    }
}
