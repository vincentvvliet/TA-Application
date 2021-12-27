package nl.tudelft.sem.Application.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.exceptions.EmptyResourceException;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.validator.IsCourseOpen;
import nl.tudelft.sem.Application.services.validator.IsGradeSufficient;
import nl.tudelft.sem.Application.services.validator.IsUniqueApplication;
import nl.tudelft.sem.Application.services.validator.Validator;
import nl.tudelft.sem.DTO.ApplyingStudentDTO;
import nl.tudelft.sem.DTO.RatingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
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
     * @return true is ratio is already met, false otherwise.
     */
    public boolean isTASpotAvailable(@SuppressWarnings("unused") UUID courseId) {
        return true;
    }

    /**
     * Creates a new TA once an application has been accepted.
     *
     * @param studentId of the student that becomes TA.
     * @param courseId of the course for which student is TA.
     *
     * @return true if the TA was successfully created.
     */
    public boolean createTA(UUID studentId, UUID courseId, int port) throws Exception {
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Mono<Boolean> accepted = webClient.post()
                .uri("/TA/createTA/" + studentId  + "/" + courseId)
                .retrieve()
                .bodyToMono(Boolean.class);
        if (accepted.blockOptional().isEmpty() || !accepted.blockOptional().get()) {
            throw new Exception("Could not create TA.");
        }
        return  true;
    }

    /** Ask the Course microservice for the grade corresponding to
     * the student and course ID of the application.
     *
     * @return A Optional double
     */
    public Double getGrade(UUID studentId, UUID courseId) throws EmptyResourceException {
        WebClient webClient = WebClient.create("http://locportalhost:47112");
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
     * @return An optional LocalDate
     */
    public LocalDate getCourseStartDate(UUID courseId) throws EmptyResourceException {
        WebClient webClient = WebClient.create("http://localhost:47112");
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
     *
     * @return rating of TA for a certain course.
     * @throws EmptyResourceException if the TA service returns an empty result.
     */
    public RatingDTO getRatingForTA(UUID studentId) throws EmptyResourceException {
        WebClient webClient = WebClient.create("http://localhost:47110");
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
                    getGradeByStudentAndCourse(a.getStudentId(), a.getCourseId()),
                    getRatingForTA(a.getStudentId()).getRating()
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
     * @return Grade of the studet.
     */
    public double getGradeByStudentAndCourse(UUID studentId, UUID courseId)
        throws EmptyResourceException {
        WebClient webClient = WebClient.create("http://localhost:47112");
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

    /** Requests notification for a user.
     *
     * @param recipientId user to recieve notification.
     * @param message of notification.
     * @param port of MS to send request to.
     */
    public boolean sendNotification(UUID recipientId, String message, int port) throws Exception {
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Mono<Boolean> accepted = webClient.post()
                .uri("/notification/createNotification" + recipientId  + "/" + message)
                .retrieve()
                .bodyToMono(Boolean.class);
        if (accepted.blockOptional().isEmpty() || !accepted.blockOptional().get()) {
            throw new Exception("Could not create notification for user.");
        }
        return  true;
    }

    /** Requests for contract to be sent to TA.
     *
     * @param studentId of TA.
     * @param courseId of course student is TAing.
     * @param port of MS to send request to.
     */
    public String sendContract(UUID studentId, UUID courseId, int port) throws Exception {
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Mono<String> contract = webClient.get()
                .uri("/contract/sendContract/" + studentId  + "/" + courseId)
                .retrieve()
                .bodyToMono(String.class);
        if (contract.blockOptional().isPresent()) {
            return contract.blockOptional().get();
        }
        throw new EmptyResourceException("Could not send contract to User");
    }

    /**
     * Create contract when a TA is hired.
     * @param studentId of the student hired.
     * @param courseId of the course the student is hired for.
     * @param port of the microservice.
     * @return the id of the contract created.
     * @throws EmptyResourceException if the contract creation failed.
     */
    public UUID createContract(UUID studentId, UUID courseId, int port) throws EmptyResourceException {
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Optional<UUID> contractId = webClient.post()
                .uri("/contract/createContract/" + studentId  + "/" + courseId)
                .retrieve()
                .bodyToMono(UUID.class)
                .blockOptional();
        if (contractId.isPresent()) {
            return contractId.get();
        }
        throw new EmptyResourceException("Contract creation failed");
    }

    /**
     * Add contract to a TA (links the two of them).
     * @param studentId of the student hired
     * @param contractId of the contract
     * @param port of the microservice
     * @return true if the action was performed.
     */
    public boolean addContract(UUID studentId, UUID contractId, int port) throws EmptyResourceException {
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Optional<Boolean> accepted = webClient.patch()
                .uri("/TA/addContract" + studentId  + "/" + contractId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .blockOptional();
        if (accepted.isEmpty() || !accepted.get()) {
            throw new EmptyResourceException("Could not link contract to TA");
        }
        return  true;

    }
}