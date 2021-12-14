package nl.tudelft.sem.Application.services;

import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.exceptions.EmptyResourceException;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.DTO.ApplyingStudentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationService {

    @Autowired
    public ApplicationRepository applicationRepository;

    /** getRatingForTA method.
     * Makes request to TA service for a rating.
     *
     * @param studentId studentId of TA we want the rating for.
     * @param courseId courseId of the course we want the rating for.
     *
     * @return rating of TA for a certain course.
     * @throws EmptyResourceException if the TA service returns an empty result.
     */
    public int getRatingForTA(UUID studentId, UUID courseId) throws EmptyResourceException {
        WebClient webClient = WebClient.create("localhost:47110");
        Mono<Integer> rating = webClient.get()
                .uri("/TA/getRating/" + studentId + "/" + courseId)
                .retrieve()
                .bodyToMono(Integer.class);
        Optional<Integer> result = rating.blockOptional();
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
        List<ApplyingStudentDTO> ret = new ArrayList<ApplyingStudentDTO>();
        for (Application a : applications) {
            try {
                ret.add(new ApplyingStudentDTO(
                        a.getStudentId(),
                        getGradeByStudentAndCourse(a.getStudentId(), a.getCourseId()),
                        Optional.of(getRatingForTA(a.getStudentId(), a.getCourseId()))
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
        WebClient webClient = WebClient.create("localhost:47112");
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





}
