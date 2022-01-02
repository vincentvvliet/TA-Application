package nl.tudelft.sem.Application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.Application.entities.Application;
import nl.tudelft.sem.Application.exceptions.EmptyResourceException;
import nl.tudelft.sem.Application.repositories.ApplicationRepository;
import nl.tudelft.sem.Application.services.strategy.EqualStrategy;
import nl.tudelft.sem.Application.services.strategy.IgnoreGradeStrategy;
import nl.tudelft.sem.Application.services.strategy.IgnoreRatingStrategy;
import nl.tudelft.sem.Application.services.strategy.StrategyContext;
import nl.tudelft.sem.Application.services.validator.IsCourseOpen;
import nl.tudelft.sem.Application.services.validator.IsGradeSufficient;
import nl.tudelft.sem.Application.services.validator.IsUniqueApplication;
import nl.tudelft.sem.Application.services.validator.Validator;
import nl.tudelft.sem.DTO.GradeDTO;
import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.DTO.RecommendationDTO;
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
        switch (strategy) {
            case "IgnoreRating":
                context.setRecommendation(new IgnoreRatingStrategy());
                break;
            case "IgnoreGrade":
                context.setRecommendation(new IgnoreGradeStrategy());
                break;
            case "Grade&Rating":
                context.setRecommendation(new EqualStrategy());
                break;
            default:
                throw new Exception("strategy doesn't exist!");
        }
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
                                                      int n) throws Exception{
        return sortOnStrategy(list, strategy).subList(0, n);
    }
}
