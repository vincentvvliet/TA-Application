package nl.tudelft.sem.Application.services;

import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.Application.exceptions.EmptyResourceException;
import nl.tudelft.sem.DTO.GradeDTO;
import nl.tudelft.sem.DTO.RatingDTO;
import nl.tudelft.sem.DTO.RecommendationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CollectionService {

    @Autowired
    private ApplicationService applicationService;

    /**
     * collectApplicationDetials method that collects a students'
     * rating and grade for a course.
     *
     * @param courseId  id of course.
     * @param studentId if of student.
     * @return recommendationDTO containing rating(or empty)
     * @throws EmptyResourceException iff no grade returned.
     */
    public RecommendationDTO collectApplicationDetails(UUID courseId, UUID studentId)
        throws Exception {
        try {
            // get rating
            RatingDTO rating = applicationService.getTARatingEmptyIfMissing(studentId, 47110);
            // get grade
            GradeDTO grade = applicationService.getGradeByCourseIdAndStudentId(
                courseId, studentId, 47112);
            // If there is no rating, have Optional.empty() as value
            Optional<Integer> ratingOpt = (rating.getRating() == null) ?
                (Optional.empty()) : (Optional.of(rating.getRating()));
            return new RecommendationDTO(
                studentId, ratingOpt, grade.getGrade());

        } catch (Exception e) {
            throw new Exception("Grade not present for student for this course");
        }
    }
}
