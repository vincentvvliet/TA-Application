package nl.tudelft.sem.Application.controllers;


import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/application/")
public class ApplicationController {

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    ApplicationService applicationService;

    /**
     * GET endpoint to retrieve an application based on studentId and courseId
     * @param studentId of student who applied
     * @param courseId of course
     * @return Application
     */
    @GetMapping("/getApplication/{studentId}/{courseId}")
    public Optional<Application> getApplication(@PathVariable(value = "studentId") UUID studentId , @PathVariable(value = "courseId") UUID courseId){
        return applicationRepository.findByStudentIdAndCourseId(studentId,courseId);
    }
}
