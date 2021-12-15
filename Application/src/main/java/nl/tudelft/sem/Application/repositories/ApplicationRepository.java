package nl.tudelft.sem.Application.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.Application.entities.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    Optional<Application> findApplicationByStudentIdAndCourseId(UUID student_id, UUID course_id);

    List<Application> findAllApplicationsByCourseId(UUID course_id);
}
