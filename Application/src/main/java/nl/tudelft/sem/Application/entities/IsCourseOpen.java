package nl.tudelft.sem.Application.entities;

import javax.management.InvalidApplicationException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

public class IsCourseOpen extends BaseValidator{
    private final long openForApplicationPeriod = 3; // how long the application period is in weeks

    /** checks if the application is made in the application period of the course
     *
     * @param application the application request that is checked
     * @return true if it is made in a valid time period
     * @throws InvalidApplicationException if not made in valid time period
     */
    @Override
    public Boolean handle(Application application) throws InvalidApplicationException {
        LocalDate current = LocalDate.now(); // get current date
        Optional<LocalDate> startCourse1 = application.getCourseStartDate(); // get startDate course from the course microservice
        if(startCourse1.isEmpty()){
            throw new InvalidApplicationException("Could not retrieve startDate that was linked to the given courseId");
        }
        LocalDate startCourse = startCourse1.get();
        if(startCourse.minusWeeks(openForApplicationPeriod).isAfter(current) // check if applications have opened yet
                && !startCourse.minusWeeks(openForApplicationPeriod).equals(current)){
            throw new InvalidApplicationException("The course is not yet open to applications");
        }
        if(current.isAfter(startCourse)){ // check if the period for application has passed
            throw new InvalidApplicationException("The period for applications has passed");
        }
        return super.checkNext(application);
    }
}