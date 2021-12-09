package nl.tudelft.sem.Application.services.validator;

import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InvalidApplicationException;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class IsCourseOpen extends BaseValidator{
    @SuppressWarnings("FieldCanBeLocal")
    private final long openForApplicationPeriod = 3; // how long the application period is in weeks

    @Autowired
    private ApplicationService applicationServices;

    /** checks if the application is made in the application period of the course
     *
     * @param application the application request that is checked
     * @return true if it is made in a valid time period
     * @throws Exception if not made in valid time period
     */
    @Override
    public Boolean handle(Application application) throws Exception {
        LocalDate current = LocalDate.now(); // get current date
        LocalDate startCourse = applicationServices.getCourseStartDate(application.getCourseId()); // get startDate course from the course microservice
        if(startCourse == null){
            throw new Exception("Could not retrieve startDate that was linked to the given courseId");
        }
        if(startCourse.minusWeeks(openForApplicationPeriod).isAfter(current) // check if applications have opened yet
                && !startCourse.minusWeeks(openForApplicationPeriod).equals(current)){
            throw new Exception("The course is not yet open to applications");
        }
        if(current.isAfter(startCourse)){ // check if the period for application has passed
            throw new Exception("The period for applications has passed");
        }
        return super.checkNext(application);
    }
}
