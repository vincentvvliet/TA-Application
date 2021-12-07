package nl.tudelft.sem.Application.controllers;

import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/application/")
public class ApplicationController {
    @Autowired
    private ApplicationRepository applicationRepository;

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
}
