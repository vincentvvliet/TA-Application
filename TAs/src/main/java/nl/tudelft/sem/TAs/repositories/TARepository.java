package nl.tudelft.sem.TAs.repositories;

import nl.tudelft.sem.TAs.entities.TA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TARepository extends JpaRepository<TA, UUID> {
    Optional<TA> findByStudentIdAndCourseId(UUID studentId, UUID courseId);
    List<TA> findAllByStudentId(UUID studentId);

    /**
     * Gets the average TA rating for a given student
     * @param studentId of the student whose average TA rating is returned
     * @return optional of average rating
     */
    @Query("SELECT AVG(ta.rating) " +
            "FROM TA ta " +
            "WHERE ta.studentId = ?1 " +
            "AND ta.rating IS NOT NULL")
    Optional<Integer> getAverageRating(UUID studentId);

    /**
     * Gets the average time spent by TAs on a given course
     * @param courseId (UUID) of the course
     * @return optional of average time spent
     */
    @Query("SELECT AVG(ta.timeSpent) " +
            "FROM TA ta " +
            "WHERE ta.courseId = ?1 " +
            "AND ta.timeSpent IS NOT NULL")
    Optional<Double> getAverageTimeSpentAsTA(UUID courseId);
}
