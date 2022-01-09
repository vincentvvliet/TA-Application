package nl.tudelft.sem.Application.services.validator;

import java.time.LocalDate;
import java.util.UUID;

import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.services.ApplicationService;
import nl.tudelft.sem.DTO.PortData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IsCourseOpen extends BaseValidator {
    @SuppressWarnings("FieldCanBeLocal")
    private final long openForApplicationPeriod = 3; // how long the application period is in weeks

    @Autowired
    private ApplicationService applicationServices;

    private PortData portData = new PortData();

    /** Checks if the application is made in the application period of the course.
     *
     * @param application the application request that is checked
     * @return true if it is made in a valid time period
     * @throws Exception if not made in valid time period
     */
    @Override
    public Boolean handle(Application application) throws Exception {
        if (application.getCourseId() == null){
            throw new Exception("The given application does not contain a course ID");
        }
        LocalDate current = LocalDate.now(); // get current date
        LocalDate startCourse = applicationServices
                .getCourseStartDate(application.getCourseId(), portData.getCoursePort()); // get startDate course from the course microservice
        if (startCourse == null) {
            throw new Exception("Could not retrieve startDate that was linked to the given courseId");
        }
        LocalDate deadline = startCourse.minusWeeks(openForApplicationPeriod);
        if (current.isAfter(deadline)) { // check if the period for application has passed
            throw new Exception("The period for applications has passed");
        }
        return super.checkNext(application);
    }
}
