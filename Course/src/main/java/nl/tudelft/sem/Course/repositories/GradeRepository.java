package nl.tudelft.sem.Course.repositories;

import nl.tudelft.sem.Course.entities.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradeRepository extends JpaRepository<Grade, UUID> {

    Optional<Grade> findByStudentIdAndCourseId(UUID student_id, UUID course_id);
}
