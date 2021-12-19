package nl.tudelft.sem.Application.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.Application.entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    Optional<Application> findByStudentIdAndCourseId(UUID student_id, UUID course_id);
    List<Application> findApplicationsByCourseId(UUID courseId);
    void deleteApplicationByStudentIdAndCourseId(UUID studentId, UUID courseId);

    @Query(value = "SELECT COUNT(Application.id) FROM Application a WHERE a.courseId = ?1 AND a.accepted = true", nativeQuery = true)
    int numberSelectedTAsForCourse(UUID course_id);
}
