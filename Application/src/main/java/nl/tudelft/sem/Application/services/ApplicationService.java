package nl.tudelft.sem.Application.services;

import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.exceptions.EmptyResourceException;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.strategy.*;
import nl.tudelft.sem.DTO.ApplyingStudentDTO;
import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.DTO.GradeDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.exceptions.EmptyResourceException;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.validator.IsCourseOpen;
import nl.tudelft.sem.Application.services.validator.IsGradeSufficient;
import nl.tudelft.sem.Application.services.validator.IsUniqueApplication;
import nl.tudelft.sem.Application.services.validator.Validator;
import nl.tudelft.sem.DTO.RecommendationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private IsCourseOpen isCourseOpen;

    @Autowired
    private IsUniqueApplication isUniqueApplication;

    @Autowired
    private IsGradeSufficient isGradeSufficient;

    private Validator validator;



    /**
     * Check if the ration of 1 TA for every 20 students is already met.
     *
     * @param courseId of the course for which the check is performed
     * @param port of the server to which request is sent (on User microservice)
     *
     * @return true if ratio is not already met (i.e. TA spot available), false otherwise.
     */
    public boolean isTASpotAvailable(UUID courseId, int port) {
        int selectedTAs = applicationRepository.numberSelectedTAsForCourse(courseId);

        WebClient webClient = WebClient.create("http://localhost:" + port);
        Mono<Integer> enrolledStudents = webClient.get()
                .uri("/course/getCourseNrParticipants/" + courseId)
                .retrieve()
                .bodyToMono(Integer.class);

        int enrolledStudentsForCourse = enrolledStudents.blockOptional().orElse(0);
        return (selectedTAs < enrolledStudentsForCourse / 20);
    }


    /**
     * Creates a new TA once an application has been accepted.
     *
     * @param studentId of the student that becomes TA.
     * @param courseId of the course for which student is TA.
     * @param port of the server to which request is sent (on TA microservice)
     *
     * @return true if the TA was successfully created.
     */
    public boolean createTA(UUID studentId, UUID courseId, int port) {
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Mono<Boolean> accepted = webClient.get()
                .uri("/TA/createTA/" + studentId  + "/" + courseId)
                .retrieve()
                .bodyToMono(Boolean.class);
        return accepted.blockOptional().orElse(false);
    }

    /** Ask the Course microservice for the grade corresponding to
     * the student and course ID of the application.
     *
     * @param studentId of the student whose grade is retrieved
     * @param courseId of the course for which the grade is retrieved
     * @param port of the server on which request is performed (on Course microservice)
     *
     * @return A Optional double
     */
    public Double getGrade(UUID studentId, UUID courseId, int port) throws EmptyResourceException {
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Mono<Double> grade = webClient.get()
                .uri("/grade/getGrade/" + studentId + "/" + courseId)
                .retrieve()
                .bodyToMono(Double.class);
        Optional<Double> result = grade.blockOptional();
        if (result.isEmpty()) {
            throw new EmptyResourceException("no grade found");
        }

        return result.get();
    }

    /** Ask the Course microservice for the startDate corresponding to
     * the course ID of the application.
     *
     * @param courseId for which start date is retrieved
     * @param port of the server on which request is performed (on Course microservice)
     *
     * @return An optional LocalDate
     */
    public LocalDate getCourseStartDate(UUID courseId, int port) throws EmptyResourceException {
        WebClient webClient = WebClient.create("http://localhost:" + port); // 47112
        Mono<LocalDate> startDate = webClient.get()
                .uri("/course/getCourseStartDate/" + courseId)
                .retrieve()
                .bodyToMono(LocalDate.class);

        Optional<LocalDate> result = startDate.blockOptional();
        if (result.isEmpty()) {
            throw new EmptyResourceException("no TA rating found");
        }
        return result.get();
    }

    /** Check if the application is valid.
     *
     * @return true if valid, false if not.
     */
    public boolean validate(Application application) {
        validator = isCourseOpen; // create chain of responsibility
        validator.setLast(isGradeSufficient);
        validator.setLast(isUniqueApplication);
        Boolean isValid = false;
        try {
            isValid = validator.handle(application);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isValid;
    }

    /** Get a list of applications by CourseId.
     *
     * @return List of Applications
     */
    public List<Application> getApplicationsByCourse(UUID course) {
        return applicationRepository.findApplicationsByCourseId(course);
    }

    /** getRatingForTA method.
     * Makes request to TA service for a average rating.
     *
     * @param studentId studentId of TA we want the rating for.
     * @param port of the server on which request is performed (on TA microservice)
     *
     * @return rating of TA for a certain course.
     * @throws EmptyResourceException if the TA service returns an empty result.
     */
    public RatingDTO getRatingForTA(UUID studentId, int port) throws EmptyResourceException {
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Mono<RatingDTO> rating = webClient.get()
            .uri("/TA/getRating/" + studentId)
            .retrieve()
            .bodyToMono(RatingDTO.class);
        Optional<RatingDTO> result = rating.blockOptional();
        if (result.isEmpty()) {
            throw new EmptyResourceException("no TA rating found");
        }

        return result.get();
    }

    /**
     * Transforms list of applications to list of ApplyingStudentDTO,
     * which contains rating and grade of student applying.
     *
     * @param applications List of bare applications.
     * @return List of detailed applications.
     */
    public List<ApplyingStudentDTO> getApplicationDetails(List<Application> applications) {
        List<ApplyingStudentDTO> ret = new ArrayList<>();
        for (Application a : applications) {
            try {
                ret.add(new ApplyingStudentDTO(
                    a.getStudentId(),
                    getGradeByStudentAndCourse(a.getStudentId(), a.getCourseId(), 47112),
                    getRatingForTA(a.getStudentId(), 47110).getRating()
                ));
            } catch (EmptyResourceException e) {
                System.out.println("failed to get application details: " + e.getMessage());
            }
        }
        return ret;
    }

    /**
     * Gets grade for a student for a specific course from course microservice.
     *
     * @param studentId id of the student.
     * @param courseId id of the course.
     * @param port of the server on which the request is performed (on the Course microservice)
     *
     * @return grade of the student
     */
    public double getGradeByStudentAndCourse(UUID studentId, UUID courseId, int port)
        throws EmptyResourceException {
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Mono<Double> rating = webClient.get()
            .uri("/grade/getGrade/" + studentId + "/" + courseId)
            .retrieve()
            .bodyToMono(Double.class);
        Optional<Double> result = rating.blockOptional();
        if (result.isEmpty()) {
            throw new EmptyResourceException("No grade for student found");
        }
        return result.get();
    }

    public List<GradeDTO> getGradesByCourseId(UUID courseId) {
        // Request to Grade microservice
        WebClient webClient = WebClient.create("http://localhost:47112");
        Flux<GradeDTO> response = webClient.get()
            .uri("/grade/getGrades/" + courseId)
            .retrieve()
            .bodyToFlux(GradeDTO.class);
        return response.toStream().collect(Collectors.toList());
    }

    public RatingDTO getRatingsByStudentId(UUID studentId) throws Exception {
        // Request to TA microservice
        // RatingOptional might be empty
        WebClient webClient = WebClient.create("http://localhost:47110");
        Mono<RatingDTO> response = webClient.get()
            .uri("/TA/getRating/" + studentId)
            .retrieve()
            .bodyToMono(RatingDTO.class);
        Optional<RatingDTO> optional = response.blockOptional();
        if (optional.isEmpty()) {
            throw new Exception("No response!");
        }
        return optional.get();
    }

    /**This method gives recommendation using the Strategy design pattern.
     * @param list of applicants to recommend.
     * @param strategy to use for recommending system.
     * @return the recommended list of applicants.
     */
    public List<RecommendationDTO> getRecommendation(List<RecommendationDTO> list, String strategy) {

        StrategyContext context = new StrategyContext();
        if(strategy.equals("IgnoreRating")) context.setRecommendation(new IgnoreRatingStrategy());
        if(strategy.equals("IgnoreGrade")) context.setRecommendation(new IgnoreGradeStrategy());
        if(strategy.equals("Grade&Rating")) context.setRecommendation(new EqualStrategy());
        return context.giveRecommendation(list);
    }
}
