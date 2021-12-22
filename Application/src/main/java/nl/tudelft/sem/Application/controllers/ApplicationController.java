package nl.tudelft.sem.Application.controllers;


import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.exceptions.EmptyResourceException;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.ApplicationService;
import nl.tudelft.sem.DTO.ApplyingStudentDTO;
import nl.tudelft.sem.DTO.RatingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/application/")
public class ApplicationController {

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    ApplicationService applicationService;

    /**
     * GET endpoint to retrieve an application based on studentId and courseId.
     *
     * @param studentId of student who applied
     * @param courseId of course
     *
     * @return Application
     */
    @GetMapping("/getApplication/{studentId}/{courseId}")
    public Optional<Application> getApplication(
        @PathVariable(value = "studentId") UUID studentId,
        @PathVariable(value = "courseId") UUID courseId) {
        return applicationRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    /** GET all applications for specific course,
     *  together with the applying student's names, grades, and possible past TA experience.
     *
     * @param course UUID for course.
     * @return list of applications for specific course.
     */

    @GetMapping("/getApplicationOverview/{course_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<ApplyingStudentDTO> getApplicationsOverviewByCourseDTO(
            @PathVariable(value = "course_id") UUID course) {
        List<Application> applications = applicationRepository.findApplicationsByCourseId(course);
        return  applicationService.getApplicationDetails(applications);
    }

    @GetMapping("/getRatings/{student_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public RatingDTO getRating(@PathVariable(value = "student_id") UUID student_id) {
        try {
            return applicationService.getRatingForTA(student_id);
        } catch (EmptyResourceException e) {
            return null;
        }
    }

    /**
     * get all applications for a course
     *
     * @param course id of course
     * @return list of applications for course.
     */
    @GetMapping("/getApplications/{course_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public Flux<Application> getApplicationsByCourse(@PathVariable(value = "course_id")
                                                                 UUID course) {
        return Flux.fromIterable(applicationService.getApplicationsByCourse(course));
    }

    /** Post endpoint creates new application using studentId and courseId
     * and return boolean of result.
     *
     * @param studentId (UUID) of the student
     * @param courseId  (UUID) of the course
     * @return boolean
     */
    @PostMapping("/createApplication/{student_id}/{course_id}")
    public Mono<Boolean> createApplicationByStudentAndCourse(
        @PathVariable(value = "student_id") UUID studentId,
        @PathVariable(value = "course_id") UUID courseId) {
        Application application = new Application(courseId, studentId);
        if (applicationService.validate(application)) {
            applicationRepository.save(application);
            return Mono.just(true);
        }
        return Mono.just(false);
    }

    /**PATCH Endpoint to accept a TA application, if the application is not yet accepted,
     * the application is closed for application and there is a TA spot available.
     *
     * @param id of the application to be accepted
     * @return true if the application was successfully accepted, false otherwise
     */

    @PatchMapping("/acceptApplication/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public Mono<Boolean> acceptApplication(@PathVariable(value = "id") UUID id) throws Exception {
        Application application = applicationRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("application does not exist"));
        if (application.isAccepted()) {
            throw new Exception("application is already accepted");
        }
        LocalDate startDate = applicationService.getCourseStartDate(application.getCourseId());
        if (LocalDate.now().isBefore(startDate.minusWeeks(3))) {
            throw new Exception("application is still open for application");
        }
        if (LocalDate.now().isAfter(startDate)) {
            throw new Exception("course has already started");
        }
        if (! applicationService.isTASpotAvailable(application.getCourseId())) {
            throw new Exception("maximum number of TA's was already reached for this course");
        }

        try {
            applicationService.createTA(application.getStudentId(), application.getCourseId(), 47110);
            applicationService.createContract(application.getStudentId(), application.getCourseId(),47110);
            applicationService.sendContract(application.getStudentId(), application.getCourseId(), 47110);
        } catch (Exception e) {
            throw new Exception("TA or contract creation failed: " + e.getMessage());
        }

        applicationService.sendNotification(application.getStudentId(), "You have been accepted for a TA position, you can expect a contract shortly.", 47111 );
        application.setAccepted(true);
        applicationRepository.save(application);

        return Mono.just(true);
    }
}
