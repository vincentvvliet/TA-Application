package nl.tudelft.sem.Application.services;


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
import nl.tudelft.sem.DTO.ApplyingStudentDTO;
import nl.tudelft.sem.DTO.GradeDTO;
import nl.tudelft.sem.portConfiguration.PortData;
import nl.tudelft.sem.DTO.RatingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
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

    private PortData portData = new PortData();

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
    public boolean createTA(UUID studentId, UUID courseId, int port) throws Exception {
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Mono<Boolean> accepted = webClient.get()
                .uri("/TA/createTA/" + studentId  + "/" + courseId)
                .retrieve()
                .bodyToMono(Boolean.class);
        if (!accepted.block()) {
            throw new Exception("Could not create TA.");
        }
        return true;
    }

    /**
     * Ask the Course microservice for the grade corresponding to
     * the student and course ID of the application.
     *
     * @param studentId of the student whose grade is retrieved
     * @param courseId of the course for which the grade is retrieved
     * @param port of the server on which request is performed (on Course microservice)
     *
     * @return Optional of grade (i.e. double)
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

    /**
     * Ask the Course microservice for the startDate corresponding to
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
            throw new EmptyResourceException("no starting date found");
        }
        return result.get();
    }

    /**
     * Check if the application is valid.
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

    /**
     * Get a list of applications by CourseId.
     *
     * @return List of Applications
     */
    public List<Application> getApplicationsByCourse(UUID course) {
        return applicationRepository.findApplicationsByCourseId(course);
    }



    /**
     * Transforms list of applications to list of ApplyingStudentDTO,
     * which contains rating and grade of student applying.
     *
     * @param applications List of bare applications.
     * @return List of detailed applications.
     */
    public List<ApplyingStudentDTO> getApplicationDetails(List<Application> applications, int gradePort, int taPort) throws Exception{
        List<ApplyingStudentDTO> ret = new ArrayList<>();
        for (Application a : applications) {
            ret.add(new ApplyingStudentDTO(
                a.getStudentId(),
                getGradeByCourseIdAndStudentId(
                    a.getStudentId(), a.getCourseId(), gradePort).getGrade(),
                Optional.of(getRatingForTA(a.getStudentId(), taPort).getRating())
            ));
        }
        return ret;
    }

    /**
     * Gets gradeDTO for a student for a specific course from course microservice.
     *
     * @param courseId  id of the course.
     * @param studentId id of the student.
     * @param port port on which Course ms is hosted. (default 47112)
     * @return GradeDTO.
     * @throws Exception when no grade is found.
     */
    public GradeDTO getGradeByCourseIdAndStudentId(UUID courseId, UUID studentId, int port)
        throws Exception {
        // Request to Course microservice
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Mono<GradeDTO> response = webClient.get()
            .uri("/grade/getGrades/" + courseId + "/" + studentId)
            .retrieve()
            .bodyToMono(GradeDTO.class);
        Optional<GradeDTO> optional = response.blockOptional();
        if (optional.isEmpty()) {
            throw new Exception("No grade found!");
        }
        return optional.get();
    }

    /**
     * getRatingForTA method.
     * Makes request to TA service for a average rating.
     *
     * @param studentId studentId of TA we want the rating for.
     * @param port port on which TA ms is hosted. (default 47110)
     * @return rating of TA for a certain course.
     * @throws Exception iff result is empty.
     */
    public RatingDTO getRatingForTA(UUID studentId, int port) throws Exception {
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Mono<RatingDTO> rating = webClient.get()
            .uri("/TA/getRating/" + studentId)
            .retrieve()
            .bodyToMono(RatingDTO.class);
        Optional<RatingDTO> result = rating.blockOptional();
        if (result.isEmpty()) {
            throw new Exception("No TA rating found!");
        }
        return result.get();
    }

    /**
     * getGradesByCourseId
     * @param courseId the id of the course
     * @return a list of all the grades students received for this course
     */
    /** Removes an application from the repository if it is actually there and
     *
     * @param studentId The ID of the student linked to the application.
     * @param courseId The ID of the course linked to the application.
     * @return A boolean of value true if it was a success and false if not
     */
    public Boolean removeApplication(UUID studentId, UUID courseId) {
        Optional<Application> application = applicationRepository
                .findByStudentIdAndCourseId(studentId, courseId);
        if (application.isEmpty()) {
            return false;
        }
        try {
            isCourseOpen.handle(application.get()); // this always throws an exception when false
        } catch (Exception exception) {
            return false;
        }
        applicationRepository.deleteApplicationByStudentIdAndCourseId(studentId,courseId);
        return true;
    }

    /**
     * Get list of courses that overlap with a certain course
     *
     * @param courseId of course that overlaps
     * @param port of the server on which request is performed (on Course microservice)
     *
     * @return list of courses that overlap with the given course
     */
    public List<UUID> getOverlappingCourses(UUID courseId, int port) {
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Flux<UUID> overlappingCourses = webClient.get()
            .uri("/course/getOverlappingCourses/" + courseId)
            .retrieve()
            .bodyToFlux(UUID.class);
        return overlappingCourses.toStream().collect(Collectors.toList());
    }

    /**
     * Determines if a student is already selected to TA 3 courses per quarter
     * Important: 3 courses are in the same quarter if they overlap (partially or totally)
     *
     * @param studentId of the student applying as TA
     * @param courseId of the course for which student is applying as TA
     *
     * @return true if student is not already TA for 3 courses this quarter, false otherwise
     */
    public boolean studentCanTAAnotherCourse(UUID studentId, UUID courseId, int coursePort) {
        List<UUID> coursesAcceptedAsTA = applicationRepository.coursesAcceptedAsTA(studentId);
        List<UUID> overlappingCourses = getOverlappingCourses(courseId, coursePort);
        overlappingCourses.retainAll(coursesAcceptedAsTA);
        return (overlappingCourses.size() < 3);
    }

    /** Sends notification to User microservice for a specified user.
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
        if (!accepted.block()) {
            throw new Exception("Could not create notification for user.");
        }
        return true;
    }

    /** Requests for contract to be sent to TA.
     *
     * @param studentId of TA.
     * @param courseId of course student is TAing.
     * @param port of MS to send request to.
     */
    public String sendContract(UUID studentId, UUID courseId, int port) throws Exception {
        WebClient webClient = WebClient.create("http://localhost:" + port);
        Optional<String> contract = webClient.get()
                .uri("/contract/sendContract/" + studentId  + "/" + courseId)
                .retrieve()
                .bodyToMono(String.class)
                .blockOptional();;
        if (contract.isPresent()) {
            return contract.get();
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
