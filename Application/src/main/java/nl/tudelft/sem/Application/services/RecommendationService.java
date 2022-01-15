package nl.tudelft.sem.Application.services;

import java.util.*;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.exceptions.EmptyResourceException;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.strategy.*;
import nl.tudelft.sem.Application.services.validator.IsCourseOpen;
import nl.tudelft.sem.Application.services.validator.IsGradeSufficient;
import nl.tudelft.sem.Application.services.validator.IsUniqueApplication;
import nl.tudelft.sem.Application.services.validator.Validator;
import nl.tudelft.sem.DTO.GradeDTO;
import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.DTO.RecommendationDTO;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {
    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CollectionService collectionService;

    /** getRecommendationDetailsByCourse.
     *
     * @param courseId id of course.
     * @return list of recommendationDTOs
     */
    public List<RecommendationDTO> getRecommendationDetailsByCourse(UUID courseId) {
        List<RecommendationDTO> result = new ArrayList<>();
        List<Application> applications = applicationRepository.findApplicationsByCourseId(courseId);
        for (Application a : applications) {
            RecommendationDTO applicationDetails;
            try {
                applicationDetails = collectionService.collectApplicationDetails(a.getCourseId(), a.getStudentId());
            } catch (Exception e) {
                // "a" doesn't have a grade for the course.
                continue;
            }
            result.add(applicationDetails);
        }
        return result;
    }


    /**
     * This method gives recommendation using the Strategy design pattern.
     *
     * @param list     of applicants to recommend.
     * @param strategy to use for recommending system.
     * @return the recommended list of applicants.
     */
    public List<RecommendationDTO> sortOnStrategy(List<RecommendationDTO> list, String strategy)
        throws Exception {
        StrategyContext context = new StrategyContext();

        context.setRecommendation(Strategy.getStrategy(strategy));

        return context.giveRecommendation(list);
    }

    /**
     * Method for recommending n students.
     *
     * @param list     of applicants to recommend.
     * @param strategy to use for recommending system.
     * @param n        amount of students.
     * @return N best students based on criterion strategy.
     */
    public List<RecommendationDTO> recommendNStudents(List<RecommendationDTO> list, String strategy,
                                                      int n) throws Exception {
        return sortOnStrategy(list, strategy).subList(0, n);
    }
}
