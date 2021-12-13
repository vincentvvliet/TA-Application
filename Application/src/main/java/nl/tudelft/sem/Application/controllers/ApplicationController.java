package nl.tudelft.sem.Application.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.DTO.ApplyingStudentDTO;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;



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

    /** GET applications for specific course.
     *
     * @param course UUID for course.
     * @return list of applications for specific course.
     */
    @GetMapping("/retrieveAll/{course_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<Application> getApplicationsByCourse(
        @PathVariable(value = "course_id") UUID course) {
        return applicationRepository.findApplicationsByCourseId(course);
    }

    /** GET all applications for specific course,
     *  together with the applying student's names, grades, and possible past TA experience.
     *
     * @param course UUID for course.
     * @return list of applications for specific course.
     */
    @GetMapping("/getApplicationOverview/{course_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public List<ApplyingStudentDTO> getApplicationsByCourseDTO(
            @PathVariable(value = "course_id") UUID course) {
        List<Application> applications = applicationRepository.findApplicationsByCourseId(course);
        return  applicationService.getApplicationDetails(applications);
    }

}
