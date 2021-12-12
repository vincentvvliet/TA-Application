package nl.tudelft.sem.Application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.Application.DTOs.ApplyingStudentDTO;
import nl.tudelft.sem.Application.DTOs.TAExperienceDTO;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.exceptions.EmptyResourceException;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

        //TODO does this have to throw an exception? no rating is valid if TA has no past experience
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
            double grade;
            int rating;
            List<TAExperienceDTO> taExperience;
            try {
                ret.add(new ApplyingStudentDTO(
                        a.getStudentId(),
                        getGradeByStudentAndCourse(a.getStudentId(), a.getCourseId()),
                        getPastTAExperience(a.getStudentId()),
                        Optional.of(getRatingForTA(a.getStudentId(), a.getCourseId()))
                ));
            } catch (EmptyResourceException e) {
                System.out.println("failed to get application details: " + e.getMessage());
            }
            //TODO: make service and controller methods in TA microservice
            // to retrieve Experience DTO
            //TODO: modify method user that requests DTOS to add names itself
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

    /**
     * Gets courses a stuent has perviously TA'd for, along with the rating for each course.
     * TODO: not usable as previously TA'd courses are currently not being stored
     *
     * @param studentId of TA.
     */
    public List<TAExperienceDTO> getPastTAExperience(UUID studentId) {
        WebClient webClient = WebClient.create("localhost:47110");
        Flux<TAExperienceDTO> rating = webClient.get()
                .uri("/TA/getExperience/" + studentId)
                .retrieve()
                .bodyToFlux(TAExperienceDTO.class);
        return rating.collectList().block();

    }




}
