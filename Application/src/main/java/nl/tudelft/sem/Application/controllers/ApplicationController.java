package nl.tudelft.sem.Application.controllers;

import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/application/")
public class ApplicationController {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicationService applicationService;

    /**
     * Post endpoint creates new application using studentId and courseId and return boolean of result
     *
     * @param studentId (UUID) of the student
     * @param courseId  (UUID) of the course
     * @return boolean
     */
    @PostMapping("/createApplication/{student_id}/{course_id}")
    public boolean createApplicationByStudentAndCourse(@PathVariable(value = "student_id") UUID studentId, @PathVariable(value = "course_id") UUID courseId) {
        Application application = new Application();
        if(application.validate()){
            applicationRepository.save(application);
            return true;
        }
        return false;
    }

//    /**
//     * GET endpoint retrieves Application by studentId and courseId
//     *
//     * @param studentId (UUID) of the student
//     * @param courseId  (UUID) of the course
//     * @return optional of grade
//     */
//    @GetMapping("/getApplication/{student_id}/{course_id}")
//    public Optional<Application> getApplicationByStudentAndCourse(@PathVariable(value = "student_id") UUID studentId, @PathVariable(value = "course_id") UUID courseId) {
//        return applicationRepository.findApplicationByStudentIdAndCourseId(studentId,courseId);
//    }

    /**PATCH Endpoint to accept a TA application
     * @param id of the application to be accepted
     * @return true if the application was successfully accepted, false otherwise
     */
    @RequestMapping("/acceptApplication/{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public boolean acceptApplication(@PathVariable(value = "id") UUID id) throws Exception {
        Application application = applicationRepository.findById(id).orElseThrow(() -> new NoSuchElementException("application does not exist"));
        if (application.isAccepted()) {
            throw new Exception("application is already accepted");
        }
        if (! applicationService.isTASpotAvailable(application.getCourseId())) {
            throw new Exception("maximum number of TA's was already reached for this course");
        }
        boolean successfullyCreated = applicationService.createTA(application.getStudentId(), application.getCourseId());
        if (! successfullyCreated) {
            return false;
        }
        application.setAccepted(true);
        applicationRepository.save(application);
        return true;
    }
}
