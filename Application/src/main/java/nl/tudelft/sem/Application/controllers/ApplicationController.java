package nl.tudelft.sem.Application.controllers;


import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.services.ValidatorService;
import nl.tudelft.sem.DTO.ApplyingStudentDTO;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.ApplicationService;
import nl.tudelft.sem.Application.services.CollectionService;
import nl.tudelft.sem.Application.services.RecommendationService;
import nl.tudelft.sem.portConfiguration.PortData;
import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.DTO.RecommendationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/application/")
public class ApplicationController {
    @Autowired
    ValidatorService validatorService;

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    ApplicationService applicationService;

    @Autowired
    RecommendationService recommendationService;

    @Autowired
    CollectionService collectionService;

    private PortData portData = new PortData();

    /**
     * GET endpoint to retrieve an application based on studentId and courseId.
     *
     * @param studentId of student who applied
     * @param courseId  of course
     * @return Application
     */
    @GetMapping("/getApplication/{studentId}/{courseId}")
    public Optional<Application> getApplication(
            @PathVariable(value = "studentId") UUID studentId,
            @PathVariable(value = "courseId") UUID courseId) {
        return applicationRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    /**
     * GET endpoint for all applications for specific course,
     * together with the applying student's names, grades, and possible past TA experience.
     *
     * @param course UUID for course.
     * @return list of applications for specific course.
     */

    @GetMapping("/getApplicationOverview/{course_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public Flux<ApplyingStudentDTO> getApplicationsOverviewByCourse(
            @PathVariable(value = "course_id") UUID course) {
        List<Application> applications = applicationRepository.findApplicationsByCourseId(course);
        try {
            return Flux
                .fromIterable(applicationService.getApplicationDetails(applications, portData.getCoursePort(), portData.getTaPort()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No applications found!");
        }
    }


    /**
     * GET endpoint for rating based on student Id.
     *
     * @param studentId studentId.
     * @return average rating for students' previous TAing experience.
     */
    @GetMapping("/getRatings/{student_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public RatingDTO getRating(@PathVariable(value = "student_id") UUID studentId) {
        try {
            return applicationService.getRatingForTA(studentId, portData.getTaPort());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * GET endpoint for Applications by course.
     *
     * @param courseId courseId.
     * @return flux of Applications.
     */
    @GetMapping("/applications/{course_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public Flux<Application> getApplicationsByCourse(@PathVariable(value = "course_id")
                                                             UUID courseId) {
        return Flux.fromIterable(applicationService.getApplicationsByCourse(courseId));
    }

    @DeleteMapping("/removeApplication/{student_id}/{course_id}")
    @ResponseStatus(value = HttpStatus.OK)
    public Mono<Boolean> removeApplication(@PathVariable(value = "student_id") UUID studentId,
                                           @PathVariable(value = "course_id") UUID courseId) {
        return Mono.just(applicationService.removeApplication(studentId, courseId));
    }

    /**
     * Post endpoint creates new application using studentId and courseId
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
        if (validatorService.validate(application)) {
            applicationRepository.save(application);
            return Mono.just(true);
        }
        return Mono.just(false);
    }

    /**
     * PATCH Endpoint to accept a TA application, if the application is not yet accepted,
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
        LocalDate startDate = applicationService.getCourseStartDate(application.getCourseId(), portData.getCoursePort());
        if (! applicationService.isSelectionPeriodOpen(startDate)) {
            throw new Exception("TA selection period is not open now");
        }
        if (!applicationService.studentCanTAAnotherCourse(application.getStudentId(), application.getCourseId(), portData.getCoursePort())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "a student can TA a maximum of 3 courses per quarter");
        }
        if (!applicationService.isTASpotAvailable(application.getCourseId(), portData.getCoursePort())) {
            throw new Exception("maximum number of TA's was already reached for this course");
        }
        return applicationService.acceptApplication(application);
    }

    /**
     * GET endpoint to get all the applications of a course and return them ordered using the specified strategy.
     * It also filters out all the grades that are below the given minimum.
     * @param courseId The id of the course all the returned applications are for.
     * @param strategy The strategy the list need to be ordered on. (check the Strategy class for more information)
     * @param minimumGrade The minimum all grades need to be above
     * @return A sorted and filtered list of applications.
     */
    @GetMapping("/getSortedListWithMinimumGrade/{course_id}/{strategy}/{minimum}")
    Flux<RecommendationDTO> getSortedListWithMinimumGrade(@PathVariable("course_id") UUID courseId,
                                                     @PathVariable("strategy") String strategy,
                                                     @PathVariable("minimum") double minimumGrade) {
        List<RecommendationDTO> list = recommendationService
                .getRecommendationDetailsByCourse(courseId)
                .stream().filter(x -> x.getGrade() >= minimumGrade).collect(Collectors.toList());
        try {
            return Flux.fromIterable(recommendationService.sortOnStrategy(list, strategy));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested Strategy not found!");
        }
    }

    /**
     * GET endpoint to get all the applications of a course and return them ordered using the specified strategy.
     * @param courseId The id of the course all the returned applications are for.
     * @param strategy The strategy the list need to be ordered on. (check the Strategy class for more information)
     * @return A sorted list of applications.
     */
    @GetMapping("/getSortedList/{course_id}/{strategy}")
    Flux<RecommendationDTO> getSortedList(@PathVariable("course_id") UUID courseId,
                                          @PathVariable("strategy") String strategy) {
        List<RecommendationDTO> list = recommendationService
                .getRecommendationDetailsByCourse(courseId);
        try {
            return Flux.fromIterable(recommendationService.sortOnStrategy(list, strategy));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested Strategy not found!");
        }
    }

    /**
     * GET endpoint to get n applications of a course and return them ordered using the specified strategy.
     * It also filters out all the grades that are below the given minimum.
     * @param courseId The id of the course all the returned applications are for.
     * @param strategy The strategy the list need to be ordered on. (check the Strategy class for more information)
     * @param n The number of applications that need to be returned.
     * @param minimumGrade The minimum all grades need to be above
     * @return A sorted and filtered list of n applications.
     */
    @GetMapping("/recommendNStudentsWithMinimumGrade/{course_id}/{n}/{strategy}/{minimum}")
    Flux<RecommendationDTO> recommendNStudentsWithMinimumGrade(@PathVariable("course_id") UUID courseId,
                                                          @PathVariable("strategy") String strategy,
                                                          @PathVariable("n") int n,
                                                          @PathVariable("minimum") double minimumGrade) {
        List<RecommendationDTO> list = recommendationService
                .getRecommendationDetailsByCourse(courseId)
                .stream().filter(x -> x.getGrade() >= minimumGrade).collect(Collectors.toList());
        try {
            return Flux.fromIterable(recommendationService.recommendNStudents(list, strategy, n));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested Strategy not found!");
        }
    }

    /**
     * GET endpoint to get n applications of a course and return them ordered using the specified strategy.
     * @param courseId The id of the course all the returned applications are for.
     * @param strategy The strategy the list need to be ordered on. (check the Strategy class for more information)
     * @param n The number of applications that need to be returned.
     * @return A sorted and filtered list of n applications.
     */
    @GetMapping("/recommendNStudents/{course_id}/{n}/{strategy}")
    Flux<RecommendationDTO> recommendNStudents(@PathVariable("course_id") UUID courseId,
                                               @PathVariable("strategy") String strategy,
                                               @PathVariable("n") int n) {
        List<RecommendationDTO> list = recommendationService
                .getRecommendationDetailsByCourse(courseId);
        try {
            return Flux.fromIterable(recommendationService.recommendNStudents(list, strategy, n));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested Strategy not found!");
        }
    }

    /**
     * PATCH endpoint to accept the top n applications of a given course using the specified strategy.
     * @param courseId The id of the course that the applications are to.
     * @param strategy The strategy used to get the top n aplications. (check the Strategy class for more information)
     * @param n The amount of applications that need to be accepted
     * @return a true in case everything works or a bad response status in case it doesn't.
     */
    @PatchMapping("/hireRecommendedN/{course_id}/{n}/{strategy}")
    Mono<Boolean> hireNStudents(@PathVariable("course_id") UUID courseId,
                                @PathVariable("strategy") String strategy,
                                @PathVariable("n") int n) {
        List<RecommendationDTO> list = recommendationService
                .getRecommendationDetailsByCourse(courseId);
        try {
            List<RecommendationDTO> recommended =
                    recommendationService.recommendNStudents(list, strategy, n);
            // Hire all n recommended students
            return hireStudents(recommended, courseId);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "StudentId in recommendationDTO not found!");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested Strategy not found!");
        }
    }

    private Mono<Boolean> hireStudents(List<RecommendationDTO> recommended, UUID courseId) throws Exception{
        for (RecommendationDTO rec : recommended) {
            Application application =
                    applicationRepository.findByStudentIdAndCourseId(rec.getStudentId(), courseId)
                            .orElseThrow(() -> new NoSuchElementException("application does not exist"));
            if (application.isAccepted()) {
                continue;
            }
            if (!applicationService.isTASpotAvailable(application.getCourseId(), portData.getTaPort())) {
                continue;
            }
            boolean successfullyCreated = applicationService
                    .createTA(application.getStudentId(), application.getCourseId(), portData.getTaPort());
            if (!successfullyCreated) {
                continue;
            }
            // Save accepted application to database.
            application.setAccepted(true);
            applicationRepository.save(application);
        }
        return Mono.just(true);
    }
}
